package tisse.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import tisse.dao.EventDao;
import tisse.dto.Dto1C;
import tisse.dto.EventDto;
import tisse.dto.EventKind;
import tisse.model.Dept;
import tisse.model.Event;
import tisse.model.Job;
import tisse.model.Person;
import tisse.service.holder.DeptHolder;
import tisse.service.holder.JobHolder;
import tisse.service.holder.PersonHolder;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static tisse.dto.EventKind.*;
import static tisse.util.Constants.LONG_PATTERN;
import static tisse.util.Constants.SHORT_PATTERN;
import static tisse.util.DateUtils.clearDate;

@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class Process1cService {

    @Inject
    private Logger logger;
    @Inject
    private PersonHolder personHolder;
    @Inject
    private DeptHolder deptHolder;
    @Inject
    private EventDao eventDao;
    @Inject
    private JobHolder jobHolder;
    @Inject
    private Integer cutoff;
    @EJB
    private EventDtoConverter eventDtoConverter;

    public List<Dto1C> process(Calendar dateLocal, List<EventDto> eventDtoList) {
        Map<Integer, Person> personMap = personHolder.getPersonMap();
        Map<Integer, Dept> deptMap = deptHolder.getMap();
        logger.info("---");

        Boolean skipInAfterOut = ObjectUtils.defaultIfNull(BooleanUtils.toBoolean(System.getProperty("bios.skip.in.after.out")), Boolean.TRUE);

        EventDto first = eventDtoList.get(0);
        String tabNum = "";
        Person person;
        int subjectId = first.getSubjectId().intValue();
        if (personMap.containsKey(subjectId)) {
            person = personMap.get(subjectId);
            tabNum = person.getTabNum();
        } else {
            String format = String.format("%d не найден", subjectId);
            logger.error(format);
            return new ArrayList<>();
        }

        boolean needJobGuid = false;

        List<Person> people = personHolder.getPersonTabNumMap().get(tabNum);
        boolean partTimeJob = isNotEmpty(people) && people.size() > 1;
        BigInteger firstSubjectId = first.getSubjectId();
        List<Event> eventsOtherPartTimer = new ArrayList<>();
        if (partTimeJob) {
            List<BigInteger> personList = findOtherPartTimersSubjectIds(people, firstSubjectId);
            eventsOtherPartTimer = eventDao.findList(dateLocal, personList);
        }
        if (partTimeJob) {
            if (!WI.equals(first.getEventKind())) {
                if (isNotEmpty(eventsOtherPartTimer)) {
                    return new ArrayList<>();
                }
            }
            if (WI.equals(first.getEventKind())) {
                if (isNotEmpty(eventsOtherPartTimer)) {
                    needJobGuid = true;
                    eventsOtherPartTimer
                            .forEach(event -> {
                                EventDto e = new EventDto();
                                e.setEventKind(event.getEventTypeValue());
                                e.setDate(event.getDate());
                                e.setDateLocal(event.getDateLocal());
                                e.setSubjectId(event.getSubjectId());
                                e.setDept(first.getDept());
                                e.setFirstName(first.getFirstName());
                                e.setLastName(first.getLastName());
                                e.setDateValue(event.getEventDate().format(DateTimeFormatter.ofPattern(LONG_PATTERN)));
                                e.setDateLocalValue(event.getEventDateLocal().format(DateTimeFormatter.ofPattern(LONG_PATTERN)));
                                e.setDateShortValue(event.getEventDate().format(DateTimeFormatter.ofPattern(SHORT_PATTERN)));
                                eventDtoList.add(e);
                            });
                }
            }
        }

        Map<Integer, Job> jobMap = jobHolder.getMap();

        BigInteger timeOut = BigInteger.valueOf(10L * 60L);

        ZoneId utc = ZoneId.of("UTC");

        Calendar dateLocalStart = (Calendar) dateLocal.clone();
        clearDate(dateLocalStart);
        dateLocalStart.add(Calendar.HOUR_OF_DAY, cutoff);
        LocalDateTime start = LocalDateTime.of(dateLocalStart.get(Calendar.YEAR), Month.of(dateLocalStart.get(Calendar.MONTH) + 1),
                dateLocalStart.get(Calendar.DAY_OF_MONTH), cutoff, 0, 0);
        long startEpoch = start.atZone(utc).toEpochSecond();
        start = Instant.ofEpochMilli(startEpoch * 1000L).atZone(utc).toLocalDateTime();
        logger.info("epoch start {}", startEpoch);
        logger.info("epoch start short {}", startEpoch);
        String startLong = start.format(DateTimeFormatter.ofPattern(LONG_PATTERN));
        String startShort = start.format(DateTimeFormatter.ofPattern(SHORT_PATTERN));

        Calendar dateLocalEnd = (Calendar) dateLocal.clone();
        clearDate(dateLocalEnd);
        dateLocalEnd.add(Calendar.DAY_OF_MONTH, 1);
        dateLocalEnd.add(Calendar.HOUR_OF_DAY, cutoff);
        LocalDateTime end = LocalDateTime.of(dateLocalEnd.get(Calendar.YEAR), Month.of(dateLocalEnd.get(Calendar.MONTH) + 1),
                dateLocalEnd.get(Calendar.DAY_OF_MONTH), cutoff, 0, 0);

        long endEpoch = end.atZone(utc).toEpochSecond();
        end = Instant.ofEpochSecond(endEpoch).atZone(utc).toLocalDateTime();
        BigInteger endTime = BigInteger.valueOf(endEpoch);
        logger.info("epoch end {}", endEpoch);
        logger.info("epoch end short {}", endEpoch);
        String endLong = end.format(DateTimeFormatter.ofPattern(LONG_PATTERN));
        String endShort = end.format(DateTimeFormatter.ofPattern(SHORT_PATTERN));

        eventDtoList.sort(Comparator.comparing(EventDto::getDateLocalValue));

        if (!WI.equals(first.getEventKind())) {
            EventKind eventKind = PE;
            EventDto last = eventDtoList.get(eventDtoList.size() - 1);
            EventDto next = findNext(last, dateLocal);
            if (null != next) {
                eventKind = WI;
            }
            EventDto e = new EventDto();
            e.setEventKind(eventKind);
            e.setDate(BigInteger.valueOf(startEpoch));
            e.setDateLocal(BigInteger.valueOf(startEpoch));
            e.setSubjectId(first.getSubjectId());
            e.setDept(first.getDept());
            e.setFirstName(first.getFirstName());
            e.setLastName(first.getLastName());
            e.setDateValue(startLong);
            e.setDateLocalValue(startLong);
            e.setDateShortValue(startShort);
            eventDtoList.add(e);
            eventDtoList.sort(Comparator.comparing(EventDto::getDateLocalValue));
        }

        EventDto last = eventDtoList.get(eventDtoList.size() - 1);
        if (!WO.equals(last.getEventKind()) && !PE.equals(last.getEventKind())) {
            EventDto next = findNext(last, dateLocal);
            EventKind eventKind = FE;
            if (null != next) {
                eventKind = WO;
            }
            EventDto e = new EventDto();
            e.setEventKind(eventKind);
            e.setDate(BigInteger.valueOf(endEpoch));
            e.setDateLocal(BigInteger.valueOf(endEpoch));
            e.setSubjectId(last.getSubjectId());
            e.setDept(last.getDept());
            e.setFirstName(last.getFirstName());
            e.setLastName(last.getLastName());
            e.setDateValue(endLong);
            e.setDateLocalValue(endLong);
            e.setDateShortValue(endShort);
            eventDtoList.add(e);
            eventDtoList.sort(Comparator.comparing(EventDto::getDateLocalValue));
        }

        String depName = first.getDepName();
        if (deptMap.containsKey(first.getDept().intValue())) {
            depName = deptMap.get(first.getDept().intValue()).getName();
        }

        List<Dto1C> dto1CS = new ArrayList<>();

        for (int i = 0; i < eventDtoList.size() - 1; i++) {
            int nextIndex = i + 1;
            EventDto current = eventDtoList.get(i);
            EventDto next = eventDtoList.get(nextIndex);

            BigInteger nextDate = next.getDateLocal();
            boolean lessThanTimeout = nextDate.subtract(current.getDateLocal()).compareTo(timeOut) < 0;
            boolean eventsAreEquals = current.getEventKind().equals(next.getEventKind());
            boolean inAfterOut = WO.equals(current.getEventKind()) && WI.equals(next.getEventKind());
            if (((eventsAreEquals && lessThanTimeout) || (inAfterOut && skipInAfterOut))) {
                continue;
            }

            LocalDateTime currentDateTime = Instant.ofEpochSecond(current.getDateLocal().longValue()).atZone(utc).toLocalDateTime();
            LocalDateTime nextDateTime;
            if (next.getEventKind().equals(EventKind.FE)) {
                nextDateTime = end;
            } else {
                nextDateTime = Instant.ofEpochSecond(nextDate.longValue()).atZone(utc).toLocalDateTime();
            }

            Dto1C dto1C = new Dto1C();
            dto1C.setDepartment(depName);
            dto1C.setTabNum(tabNum);
            dto1C.setFirstName(current.getFirstName());
            dto1C.setLastName(current.getLastName());
            dto1C.setDate(current.getDateShortValue());
            dto1C.setDateFirst(current.getDateLocalValue());
            Duration duration;
            if (nextDate.compareTo(endTime) == 0) {
                duration = Duration.between(currentDateTime, end);
                dto1C.setDateNext(endLong);
            } else {
                duration = Duration.between(currentDateTime, nextDateTime);
                dto1C.setDateNext(next.getDateLocalValue());
            }
            BigInteger subtract = BigInteger.valueOf(duration.toMinutes());
            dto1C.setMinutes(subtract.intValue());
            if (WI.equals(current.getEventKind()) || DO.equals(current.getEventKind())) {
                dto1C.setStatus("work");
                dto1C.setState("0");
            } else if (WO.equals(current.getEventKind()) || PE.equals(current.getEventKind())) {
                dto1C.setStatus("free");
                dto1C.setState("0");
            } else if (DI.equals(current.getEventKind())) {
                dto1C.setStatus("dinner");
                dto1C.setState("1");
            }

            dto1C.setDescription(String.format("%s_%s -> %s_%s", current.getEventKind().name(), current.getSubjectId(), next.getEventKind().name(), next.getSubjectId()));
            if (FE.equals(next.getEventKind()) ||
                    WI.equals(next.getEventKind()) ||
                    PE.equals(current.getEventKind()) ||
                    eventsAreEquals ||
                    (DI.equals(current.getEventKind()) && !DO.equals(next.getEventKind()))) {
                dto1C.setMark("M");
            } else {
                dto1C.setMark("D");
            }
            dto1C.setJobGuid("");
            if (partTimeJob && needJobGuid) {
                Integer keyJob = Integer.valueOf(person.getJobId());
                if (jobMap.containsKey(keyJob)) {
                    Job job = jobMap.get(keyJob);
                    dto1C.setJobGuid(job.getGuid1c());
                }
            }
            dto1CS.add(dto1C);
        }

        return dto1CS;
    }

    private List<BigInteger> findOtherPartTimersSubjectIds(List<Person> people, BigInteger firstSubjectId) {
        return people.stream()
                .filter(p -> firstSubjectId.compareTo(BigInteger.valueOf(p.getId())) != 0)
                .map(p -> BigInteger.valueOf(p.getId()))
                .collect(Collectors.toList());
    }

    private List<EventDto> findPreviousList(EventDto first, Calendar dateLocal) {
        List<Event> events = new ArrayList<>();
        Calendar lastCalendar = (Calendar) dateLocal.clone();
        clearDate(lastCalendar);
        lastCalendar.add(Calendar.DAY_OF_MONTH, 1);
        lastCalendar.set(Calendar.HOUR_OF_DAY, cutoff);
        Event previousIn = eventDao.findPreviousIn(first.getObjectId(), first.getSubjectId(), first.getId(), null);
        if (null != previousIn) {
            events = eventDao.findPrevious(first.getObjectId(), first.getSubjectId(), first.getId(), previousIn.getId(), null);
        }
        return (events.stream().map(event -> eventDtoConverter.convert(event)).collect(Collectors.toList()));
    }

    private EventDto findNext(EventDto last, Calendar dateLocal) {
        EventDto eventDto = null;

        Calendar lastCalendar = (Calendar) dateLocal.clone();
        clearDate(lastCalendar);
        lastCalendar.add(Calendar.DAY_OF_MONTH, 1);
        lastCalendar.set(Calendar.HOUR_OF_DAY, 9);

        Event nextEvent = eventDao.findNextEvent(last.getObjectId(), last.getSubjectId(), last.getId(), lastCalendar);
        if (null != nextEvent) {
            eventDto = eventDtoConverter.convert(nextEvent);
        }
        return eventDto;
    }

}
