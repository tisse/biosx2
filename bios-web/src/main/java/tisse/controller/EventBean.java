package tisse.controller;


import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import tisse.dto.EventDto;
import tisse.filter.EventFilter;
import tisse.service.EventService;
import tisse.service.mail.MailSettingHolder;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@RequestScoped
@ManagedBean
public class EventBean extends BaseBean{

    @Inject
    private Logger logger;

    @Inject
    private EventService eventService;
    @Inject
    private MailSettingHolder mailSettingHolder;

    private Calendar date;
    private BigInteger depId;
    private Boolean exportExcel;
    private Boolean export1c;

    private List<EventDto> events;

    @PostConstruct
    private void init() {
        date = Calendar.getInstance();
    }

    public void search() {
        EventFilter eventFilter = prepareEventFilter();
        Map<BigInteger, List<EventDto>> map = eventService.process(eventFilter);
        logger.info("{}", map.keySet());
        events = map.get(depId);
        if (null != events) {
            logger.info("{}", events.size());
        } else {
            logger.info("0");
        }
    }

    private EventFilter prepareEventFilter() {
        EventFilter eventFilter = new EventFilter();
        eventFilter.setDepId(depId);
        eventFilter.setDateLocal(date);
        logger.info("{}", eventFilter);
        return eventFilter;
    }

    public void excelMail() {
        EventFilter eventFilter = prepareEventFilter();
        exportExcel = eventService.mailExcel(eventFilter);
        if (BooleanUtils.isTrue(exportExcel)){
            infoMessage("Отправка на почту успешна");
        } else {
            errorMessage("Отправка на почту неуспешна");
        }
        logger.info("exportExcel {}", exportExcel);
    }

    public void csv1c() {
        EventFilter eventFilter = prepareEventFilter();
        export1c = eventService.ftp1C(eventFilter);
        if (BooleanUtils.isTrue(export1c)){
            infoMessage("Выгрузка успешна");
        } else {
            errorMessage("Выгрузка неуспешна");
        }
        logger.info("export1c {}", export1c);
    }

    public boolean isMailEnabled() {
        return mailSettingHolder.getMailSetting().isEnabled();
    }

    public List<EventDto> getEvents() {
        return events;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public BigInteger getDepId() {
        return depId;
    }

    public void setDepId(BigInteger depId) {
        this.depId = depId;
    }

    public Boolean getExportExcel() {
        return exportExcel;
    }

    public void setExportExcel(Boolean exportExcel) {
        this.exportExcel = exportExcel;
    }

    public Boolean getExport1c() {
        return export1c;
    }

    public void setExport1c(Boolean export1c) {
        this.export1c = export1c;
    }

    @Override
    public String getTitle() {
        return "Ручная выгрузка";
    }
}
