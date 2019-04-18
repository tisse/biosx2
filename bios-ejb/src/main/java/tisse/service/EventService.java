package tisse.service;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import tisse.dao.EventDao;
import tisse.dto.*;
import tisse.filter.EventFilter;
import tisse.model.Dept;
import tisse.model.DeptOld;
import tisse.model.Event;
import tisse.model.Person;
import tisse.service.holder.*;
import tisse.service.mail.MailProcessor;
import tisse.service.mail.MailSender;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static tisse.util.Constants.SHORT_PATTERN;
import static tisse.util.DateUtils.formatCurrent;

@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class EventService {

    private static final String XLSX = ".xlsx";
    private static final String SEP_DOT = ".";
    private static final String PATTERN = "yyyyMMdd";
    private static final BigInteger BIG_INTEGER_THOUSAND = BigInteger.valueOf(1000);

    @Inject
    private Logger logger;
    @Inject
    private EventDao eventDao;
    @Inject
    private PersonHolder personHolder;
    @Inject
    private DeptHolder deptHolder;
    @Inject
    private DeptOldHolder deptOldHolder;
    @EJB
    private PersonDepHolder personDepHolder;
    @Inject
    private ExcelWriter excelWriter;
    @Inject
    private Csv1CWriter csv1CWriter;
    @Inject
    private MailAddressHolder mailAddressHolder;
    @Inject
    private MailSender mailSender;
    @EJB
    private DepTrackHolder depTrackHolder;
    @EJB
    private MailProcessor mailProcessor;
    @EJB
    private Process1cService process1cService;
    @EJB
    private EventDtoConverter eventDtoConverter;

    public boolean trackFull(EventFilter filter) {
        String startTimeFormatted = formatCurrent();
        logger.info("process log tracker start {}", startTimeFormatted);

        personHolder.refresh();
        personDepHolder.refresh();
        deptHolder.refresh();
        deptOldHolder.refresh();

        List<BigInteger> subjectIds = depTrackHolder.getWorkerIds(filter.getDateLocal());
        if (isNotEmpty(subjectIds)) {
            filter.setSubjectIds(new HashSet<>(subjectIds));
        }

        Map<BigInteger, List<EventDto>> map = search(filter);
        processFtp1C(map, filter.getDateLocal());
        mailProcessor.processMailExcel(filter, map);
        String finishTimeFormatted = formatCurrent();
        logger.info("process log tracker finish {}", finishTimeFormatted);

        String body = String.format("<h3>full log</h3> start: <b>%s</b><br/>finish: <b>%s</b>", startTimeFormatted, finishTimeFormatted);
        mailSender.logTrackInfo(body);
        return true;
    }

    public boolean trackExcel(EventFilter filter) {
        String startTimeFormatted = formatCurrent();
        logger.info("process log tracker start {}", startTimeFormatted);

        personHolder.refresh();
        personDepHolder.refresh();
        deptHolder.refresh();
        deptOldHolder.refresh();

        List<BigInteger> subjectIds = depTrackHolder.getWorkerIds(filter.getDateLocal());
        if (isNotEmpty(subjectIds)) {
            filter.setSubjectIds(new HashSet<>(subjectIds));
        }

        Map<BigInteger, List<EventDto>> map = search(filter);
        mailProcessor.processExcel(filter, map);
        String finishTimeFormatted = formatCurrent();
        logger.info("process log tracker finish {}", finishTimeFormatted);

        String body = String.format("<h3>full log</h3> start: <b>%s</b><br/>finish: <b>%s</b>", startTimeFormatted, finishTimeFormatted);
        mailSender.logTrackInfo(body);
        return true;
    }


    public Map<BigInteger, List<EventDto>> process(EventFilter eventFilter) {
        Map<BigInteger, List<EventDto>> map = search(eventFilter, null);
        map.forEach((bigInteger, eventDtos) -> {
            Map<BigInteger, List<EventDto>> eventDtoMap = eventDtos.stream().collect(Collectors.groupingBy(EventDto::getSubjectId));
            eventDtoMap.forEach((tabNum, eventDtoList) -> process1cService.process(eventFilter.getDateLocal(), eventDtoList));
        });
        return map;
    }

    public boolean export1C(EventFilter eventFilter) {
        Map<BigInteger, List<EventDto>> map = search(eventFilter, null);

        String yyyyMMdd = DateFormatUtils.format(eventFilter.getDateLocal(), PATTERN);
        String biosWorkDir = System.getProperty("bios.workdir");
        map.forEach((bigInteger, eventDtos) -> {
            logger.info("eventDtos {}", eventDtos.size());
            Map<BigInteger, List<EventDto>> eventDtoMap = eventDtos.stream().collect(Collectors.groupingBy(EventDto::getSubjectId));
            eventDtoMap.forEach((tabNum, eventDtoList) -> {
                byte[] process = csv1CWriter.process(process1cService.process(eventFilter.getDateLocal(), eventDtoList));
                String name = String.format("%s%s%s%s.csv", biosWorkDir, bigInteger, SEP_DOT, yyyyMMdd);
                try {
                    Files.write(Paths.get(name), process);
                } catch (IOException e) {
                    logger.error("", e);
                }
            });
        });
        return MapUtils.isNotEmpty(map);
    }

    public boolean ftp1C(EventFilter eventFilter) {
        Map<Integer, Dept> deptMap = deptHolder.getMap();

        int depId = eventFilter.getDepId().intValue();
        Dept dept = deptMap.get(depId);

        List<BigInteger> subjectIds = depTrackHolder.getWorkerIds(eventFilter.getDateLocal(), Collections.singletonList(depId));

        if (isNotEmpty(subjectIds)) {
            logger.info("exists subjectIds {}", subjectIds);
            eventFilter.setSubjectIds(new HashSet<>(subjectIds));
        }

        Map<BigInteger, List<EventDto>> map = search(eventFilter, dept);
        processFtp1C(map, eventFilter.getDateLocal());
        return MapUtils.isNotEmpty(map);
    }

    private void doFtpBuffer(Map<BigInteger, List<EventDto>> map, Calendar dateLocal) {
        String yyyyMMdd = DateFormatUtils.format(dateLocal, "yyyy_MM_dd");
        Map<Integer, DeptOld> deptOldMap = deptOldHolder.getDeptOldMap();
        String dirName = System.getProperty("bios.ftp.buffer");

        map.forEach((depId, eventDtos) -> {
            DeptOld dept = deptOldMap.get(depId.intValue());
            List<Dto1C> dtos = new ArrayList<>();
            Map<BigInteger, List<EventDto>> collect = eventDtos.stream().collect(Collectors.groupingBy(EventDto::getSubjectId));
            collect.forEach((subjectId, eventDtoList) -> {
                eventDtoList = eventDtoList.stream().sorted(new EventDtoComparator()).collect(Collectors.toList());
                dtos.addAll(process1cService.process(dateLocal, eventDtoList));
            });
            byte[] bytes = csv1CWriter.process(dtos);
            MailData mailData = new MailData();
            mailData.setContent(bytes);
            String name = String.format("%s_Time_Export_%s.csv", dept.getGuid1c(), yyyyMMdd);
            mailData.setName(name);
            try {
                Files.write(Paths.get(dirName.concat(name)), bytes);
                logger.info("ftp {} ok", mailData.getName());
            } catch (IOException e) {
                logger.error("", e);
            }
        });
    }

    public void processFtp1C(Map<BigInteger, List<EventDto>> map, Calendar dateLocal) {
        doFtpBuffer(map, dateLocal);
    }

    public boolean exportExcel(EventFilter eventFilter) {
        Map<BigInteger, List<EventDto>> map = search(eventFilter);
        String yyyyMMdd = DateFormatUtils.format(eventFilter.getDateLocal(), PATTERN);
        String biosWorkDir = System.getProperty("bios.workdir");
        map.forEach((bigInteger, eventDtos) -> {
            byte[] process = excelWriter.process(convertDtoExcels(eventDtos));
            try {
                Files.write(Paths.get(biosWorkDir + bigInteger + SEP_DOT + yyyyMMdd + XLSX), process);
            } catch (IOException e) {
                logger.error("{}", e);
            }
        });
        return MapUtils.isNotEmpty(map);
    }

    public boolean mailExcel(EventFilter eventFilter) {
        Map<BigInteger, List<EventDto>> map = search(eventFilter);
        mailProcessor.processMailExcel(eventFilter, map);
        return MapUtils.isNotEmpty(map);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void processMailExcel(EventFilter eventFilter, Map<BigInteger, List<EventDto>> map) {
        String yyyyMMdd = DateFormatUtils.format(eventFilter.getDateLocal(), PATTERN);
        Map<Integer, DeptOld> deptOldMap = deptOldHolder.getDeptOldMap();
        map.forEach((depId, eventDtos) -> {
            DeptOld dept = deptOldMap.get(depId.intValue());
            List<String> address = mailAddressHolder.getMail(dept.getGuid1c());
            byte[] bytes = excelWriter.process(convertDtoExcels(eventDtos));
            MailData mailData = new MailData();
            mailData.setContent(bytes);
            mailData.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            mailData.setName(dept.getName().concat(SEP_DOT).concat(yyyyMMdd).concat(XLSX));
            String subject = String.format("%s с %s по %s", dept.getName(), getFormattedDateStart(eventFilter), getFormattedDateEnd(eventFilter));
            logger.info("{}", address);
            logger.info("{}", subject);
            mailSender.send(address, subject, subject, mailData);
        });
    }

    private String getFormattedDateStart(EventFilter eventFilter) {
        return DateFormatUtils.format(eventFilter.getDateLocal(), SHORT_PATTERN);
    }

    private String getFormattedDateEnd(EventFilter eventFilter) {
        Calendar dateLocalEnd = (Calendar) eventFilter.getDateLocal().clone();
        dateLocalEnd.add(Calendar.DAY_OF_YEAR, 1);
        return DateFormatUtils.format(dateLocalEnd, SHORT_PATTERN);
    }

    private List<DtoExcel> convertDtoExcels(List<EventDto> eventDtos) {
        return eventDtos.stream().map(this::convertExcel).collect(Collectors.toList());
    }

    private DtoExcel convertExcel(EventDto eventDto) {
        Map<Integer, Person> personMap = personHolder.getPersonMap();
        String tabNum = "";
        int subjectId = eventDto.getSubjectId().intValue();
        if (personMap.containsKey(subjectId)) {
            Person person = personMap.get(subjectId);
            tabNum = person.getTabNum();
            logger.info("person {} {} {} - {}", person.getLastName(), person.getFirstName(), person.getId(), person.getJobId());
        }

        DtoExcel dtoExcel = new DtoExcel();
        dtoExcel.setDepartment(eventDto.getDepName());
        dtoExcel.setFirstName(eventDto.getFirstName());
        dtoExcel.setLastName(eventDto.getLastName());
        dtoExcel.setTabNum(tabNum);

        ZoneId utc = ZoneId.of("UTC");
        LocalDateTime currentDateTime = Instant.ofEpochMilli(eventDto.getDateLocal().multiply(BIG_INTEGER_THOUSAND).longValue()).atZone(utc).toLocalDateTime();

        String date = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        switch (eventDto.getEventType()) {
            case EVENT_IN:
            case EVENT_CARD_IN:
                dtoExcel.setWorkIn(date);
                break;
            case EVENT_OUT:
            case EVENT_CARD_OUT:
                dtoExcel.setWorkOut(date);
                break;
            case EVENT_TO_LAUNCH:
                dtoExcel.setDinnerIn(date);
                break;
            case EVENT_FROM_LAUNCH:
                dtoExcel.setDinnerOut(date);
                break;
            default:
                break;
        }
        return dtoExcel;
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Map<BigInteger, List<EventDto>> search(EventFilter eventFilter) {
        List<BigInteger> subjectIds;
        if (isNotEmpty(eventFilter.getDepIds())) {
            subjectIds = depTrackHolder.getWorkerIds(eventFilter.getDateLocal(), eventFilter.getDepIds().stream().map(BigInteger::intValue).collect(Collectors.toList()));
        } else if (null != eventFilter.getDepId()) {
            subjectIds = depTrackHolder.getWorkerIds(eventFilter.getDateLocal(), Collections.singletonList(eventFilter.getDepId().intValue()));
        } else {
            subjectIds = depTrackHolder.getWorkerIds(eventFilter.getDateLocal());
        }
        HashSet<BigInteger> hashSet = new HashSet<>(subjectIds);

        List<Event> list = new ArrayList<>();
        List<List<BigInteger>> partitionLists = getPartitionLists(hashSet, 200);
        for (List<BigInteger> bigIntegers : partitionLists) {
            eventFilter.setSubjectIds(new HashSet<>(bigIntegers));
            list.addAll(eventDao.list(eventFilter));
        }
        List<EventDto> eventDtoList = convert(list);
        return eventDtoList.stream().filter(eventDto -> null != eventDto.getDept()).collect(Collectors.groupingBy(EventDto::getDept));
    }

    private int getPartitionCount(int collectionSize, int partitionSize) {
        if (collectionSize > partitionSize) {
            int remainder = collectionSize % partitionSize;
            return remainder != 0 ? (collectionSize - remainder) / partitionSize + 1 : collectionSize / partitionSize;
        } else {
            return 1;
        }
    }

    private <T> List<List<T>> getPartitionLists(Collection<T> rootCollection, int partitionSize) {
        if (isNotEmpty(rootCollection)) {
            int partitionCount = getPartitionCount(rootCollection.size(), partitionSize);
            List<List<T>> lists = new ArrayList<>(partitionCount);
            for (int i = 0; i < partitionCount; i++) {
                lists.add(new ArrayList<>());
            }

            int index = 0;
            for (T object : rootCollection) {
                lists.get(index++ % partitionCount).add(object);
            }

            return lists;
        }

        return Collections.emptyList();
    }


    private Map<BigInteger, List<EventDto>> search(EventFilter eventFilter, Dept dept) {
        List<Event> list = eventDao.list(eventFilter);
        List<EventDto> eventDtoList = convert(list);
        if (null == dept) {
            return eventDtoList.stream().collect(Collectors.groupingBy(EventDto::getDept));
        } else {
            Map<BigInteger, List<EventDto>> map = new HashMap<>();
            map.put(BigInteger.valueOf(dept.getId()), eventDtoList);
            return map;
        }
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    private List<EventDto> convert(List<Event> events) {
        return events.stream()
                .map(event -> eventDtoConverter.convert(event))
                .sorted(new EventDtoComparator())
                .collect(Collectors.toList());
    }

}
