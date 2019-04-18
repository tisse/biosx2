package tisse.service.mail;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import tisse.dto.DtoExcel;
import tisse.dto.EventDto;
import tisse.dto.MailData;
import tisse.filter.EventFilter;
import tisse.model.DeptOld;
import tisse.model.Person;
import tisse.service.ExcelWriter;
import tisse.service.holder.DeptOldHolder;
import tisse.service.holder.MailAddressHolder;
import tisse.service.holder.PersonHolder;

import javax.ejb.Asynchronous;
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
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static tisse.util.Constants.SHORT_PATTERN;

@Stateless
public class MailProcessor {

    private static final String PATTERN = "yyyyMMdd";
    private static final String XLSX = ".xlsx";
    private static final String SEP_DOT = ".";
    private static final BigInteger BIG_INTEGER_THOUSAND = BigInteger.valueOf(1000);

    @Inject
    private MailAddressHolder mailAddressHolder;
    @Inject
    private DeptOldHolder deptOldHolder;
    @Inject
    private PersonHolder personHolder;
    @Inject
    private ExcelWriter excelWriter;
    @Inject
    private Logger logger;
    @Inject
    private MailSender mailSender;


    @TransactionAttribute(TransactionAttributeType.NEVER)
    @Asynchronous
    public void processMailExcel(EventFilter eventFilter, Map<BigInteger, List<EventDto>> map) {
        String yyyyMMdd = DateFormatUtils.format(eventFilter.getDateLocal(), PATTERN);
        Map<Integer, DeptOld> deptOldMap = deptOldHolder.getDeptOldMap();
        map.forEach((depId, eventDtos) -> {
            DeptOld dept = deptOldMap.get(depId.intValue());
            logger.info("process mail  to {}", dept.getName());
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

    @TransactionAttribute(TransactionAttributeType.NEVER)
    @Asynchronous
    public void processExcel(EventFilter eventFilter, Map<BigInteger, List<EventDto>> map) {
        String dirName = System.getProperty("bios.ftp.buffer");
        String yyyyMMdd = DateFormatUtils.format(eventFilter.getDateLocal(), PATTERN);
        String folder = yyyyMMdd;
        try {
            Files.createDirectories(Paths.get(dirName.concat(folder)));
        } catch (IOException e) {
            logger.error("", e);
        }
        Map<Integer, DeptOld> deptOldMap = deptOldHolder.getDeptOldMap();
        map.forEach((depId, eventDtos) -> {
            DeptOld dept = deptOldMap.get(depId.intValue());
            logger.info("process excel to {}", dept.getName());
            byte[] bytes = excelWriter.process(convertDtoExcels(eventDtos));
            try {
                String fileName = dept.getName().concat(SEP_DOT).concat(yyyyMMdd).concat(XLSX);
                Files.write(Paths.get(dirName.concat(folder).concat("/").concat(fileName)), bytes);
                logger.info("excel {} ok", fileName);
            } catch (IOException e) {
                logger.error("", e);
            }
        });
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


    private String getFormattedDateStart(EventFilter eventFilter) {
        return DateFormatUtils.format(eventFilter.getDateLocal(), SHORT_PATTERN);
    }

    private String getFormattedDateEnd(EventFilter eventFilter) {
        Calendar dateLocalEnd = (Calendar) eventFilter.getDateLocal().clone();
        dateLocalEnd.add(Calendar.DAY_OF_YEAR, 1);
        return DateFormatUtils.format(dateLocalEnd, SHORT_PATTERN);
    }


}
