package tisse.controller;


import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import tisse.filter.EventFilter;
import tisse.service.DepTracker;
import tisse.service.EventService;
import tisse.service.FtpBufferService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import java.util.Calendar;

@ManagedBean
public class TrackBean extends BaseBean{

    @Inject
    private Logger logger;

    @Inject
    private EventService eventService;
    @Inject
    private DepTracker depTracker;
    @Inject
    private FtpBufferService ftpBufferService;

    private Calendar date;

    @PostConstruct
    private void init() {
        date = Calendar.getInstance();
    }

    public void trackFull() {
        boolean b = eventService.trackFull(prepareEventFilter());
        if (BooleanUtils.isTrue(b)){
            infoMessage("Выгрузка успешна");
        } else {
            errorMessage("Выгрузка неуспешна");
        }
    }

    public void trackExcel() {
        boolean b = eventService.trackExcel(prepareEventFilter());
        if (BooleanUtils.isTrue(b)){
            infoMessage("Выгрузка успешна");
        } else {
            errorMessage("Выгрузка неуспешна");
        }
    }

    public void buffer() {
        ftpBufferService.process();
    }
    public void dep() {
        depTracker.track();
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    private EventFilter prepareEventFilter() {
        EventFilter eventFilter = new EventFilter();
        eventFilter.setDateLocal(date);
        logger.info("{}", eventFilter);
        return eventFilter;
    }

    @Override
    public String getTitle() {
        return "Выгрузка";
    }
}
