package tisse.service;


import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import tisse.dao.EventDao;
import tisse.dto.EventDto;
import tisse.dto.TimeShift;
import tisse.filter.EventFilter;
import tisse.model.Event;
import tisse.service.holder.*;
import tisse.service.mail.MailProcessor;
import tisse.service.mail.MailSender;

import javax.ejb.*;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static tisse.util.DateUtils.formatCurrent;

@Singleton
@TransactionAttribute(TransactionAttributeType.NEVER)
public class LogTracker {

    private static final long WAIT = 1000L * 60L * 60L;

    private static final Integer BASE_HOUR = 9;

    @Inject
    private Logger logger;
    @EJB
    private EventService eventService;
    @EJB
    private MailProcessor mailProcessor;
    @EJB
    private EventDao eventDao;
    @EJB
    private MailSender mailSender;
    @EJB
    private PersonDepHolder personDepHolder;
    @EJB
    private TimeShiftHolder timeShiftHolder;
    @EJB
    private PersonHolder personHolder;
    @EJB
    private DeptHolder deptHolder;
    @EJB
    private DepTrackHolder depTrackHolder;
    @EJB
    private DeptOldHolder deptOldHolder;

    @Schedule(hour = "*")
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void track() {
        timeShiftHolder.refresh();
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int shift = hour - BASE_HOUR;

        List<TimeShift> timeShifts = timeShiftHolder.get(shift);

        if (CollectionUtils.isNotEmpty(timeShifts)) {
            String startTimeFormatted = formatCurrent();
            logger.info("process log tracker start {}", startTimeFormatted);

            synchronized (this) {
                logger.info("check DB access");
                Event last = eventDao.findLast();

                while (null == last) {
                    logger.info("fail DB access");
                    try {
                        wait(WAIT);
                        last = eventDao.findLast();
                    } catch (InterruptedException e) {
                        logger.error("", e);
                    }
                }
                logger.info("ok DB access");
            }

            personHolder.refresh();
            personDepHolder.refresh();
            deptHolder.refresh();
            deptOldHolder.refresh();

            EventFilter eventFilter = prepareEventFilter(timeShifts);

            if (null != eventFilter) {
                Map<BigInteger, List<EventDto>> map = eventService.search(eventFilter);
                eventService.processFtp1C(map, eventFilter.getDateLocal());
                mailProcessor.processMailExcel(eventFilter, map);
                String finishTimeFormatted = formatCurrent();
                logger.info("process log tracker finish {}", finishTimeFormatted);

                String body = String.format("start: <b>%s</b><br/>finish: <b>%s</b>", startTimeFormatted, finishTimeFormatted);
                mailSender.logTrackInfo(body);
            } else {
                String body = String.format("hour %d no depts by time shift data", hour);
                mailSender.logTrackInfo(body);
            }

        } else {
            String body = String.format("hour %d skipped - no depts", hour);
            mailSender.logTrackInfo(body);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void trackFull(EventFilter filter) {
        String startTimeFormatted = formatCurrent();
        logger.info("process log tracker start {}", startTimeFormatted);

        personHolder.refresh();
        personDepHolder.refresh();
        deptHolder.refresh();
        deptOldHolder.refresh();

        EventFilter eventFilter = prepareEventFilter(filter);

        Map<BigInteger, List<EventDto>> map = eventService.search(eventFilter);
        eventService.processFtp1C(map, eventFilter.getDateLocal());
        mailProcessor.processMailExcel(eventFilter, map);
        String finishTimeFormatted = formatCurrent();
        logger.info("process log tracker finish {}", finishTimeFormatted);

        String body = String.format("start: <b>%s</b><br/>finish: <b>%s</b>", startTimeFormatted, finishTimeFormatted);
        mailSender.logTrackInfo(body);
    }

    private EventFilter prepareEventFilter(List<TimeShift> timeShifts) {
        HashSet<BigInteger> depIds = new HashSet<>();
        List<String> depGuids = timeShifts.stream().map(TimeShift::getDepGuId).collect(Collectors.toList());
        for (String depGuid : depGuids) {
            if (deptOldHolder.getDeptOldGuidMap().containsKey(depGuid)) {
                depIds.add(BigInteger.valueOf(deptOldHolder.getDeptOldGuidMap().get(depGuid).getId()));
            }
        }

        if (CollectionUtils.isEmpty(depIds)) {
            logger.warn("no depts by time shift data");
            return null;
        }

        Calendar calendar = getCalendarYesterday();
        List<BigInteger> subjectIds = depTrackHolder.getWorkerIds(calendar, depIds.stream().map(BigInteger::intValue).collect(Collectors.toList()));

        EventFilter eventFilter = new EventFilter();
        eventFilter.setDateLocal(calendar);
        eventFilter.setDepIds(depIds);

        if (CollectionUtils.isNotEmpty(subjectIds)) {
            eventFilter.setSubjectIds(new HashSet<>(subjectIds));
        }

        logger.info("{}", eventFilter);
        return eventFilter;
    }

    private Calendar getCalendarYesterday() {
        Calendar calendar = Calendar.getInstance();
        clearDate(calendar);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return calendar;
    }

    private EventFilter prepareEventFilter(EventFilter filter) {
        List<BigInteger> subjectIds = depTrackHolder.getWorkerIds(filter.getDateLocal());
        if (CollectionUtils.isNotEmpty(subjectIds)) {
            filter.setSubjectIds(new HashSet<>(subjectIds));
        }
        return filter;
    }

    private void clearDate(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
    }

}
