package tisse.controller;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import tisse.dto.EventDto;
import tisse.dto.EventDtoReverseComparator;
import tisse.filter.EventFilter;
import tisse.model.DeptOld;
import tisse.service.EventService;
import tisse.service.holder.DeptOldHolder;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public class DataBean extends BaseBean {

    @Inject
    private Logger logger;

    @Inject
    private DeptOldHolder deptOldHolder;
    @Inject
    private EventService eventService;

    private String title;
    private DeptOld dept;
    private String timeValue;
    private List<EventDto> eventDtos;

    public void process() {
        eventDtos = new ArrayList<>();
        timeValue = DateFormatUtils.format(Calendar.getInstance(), "dd.MM.yyyy HH:mm:ss");
        processToday();
        processBeforeToday();
        if (CollectionUtils.isNotEmpty(eventDtos)) {
            eventDtos.sort(new EventDtoReverseComparator());
        }
    }

    private void processToday() {
        Map<BigInteger, List<EventDto>> map = eventService.search(prepareEventFilter());
        if (MapUtils.isNotEmpty(map) && map.containsKey(BigInteger.valueOf(dept.getId()))) {
            eventDtos.addAll(map.get(BigInteger.valueOf(dept.getId())));
        }
    }

    private void processBeforeToday() {
        Map<BigInteger, List<EventDto>> map = eventService.search(prepareBeforeTodayEventFilter());
        if (MapUtils.isNotEmpty(map) && map.containsKey(BigInteger.valueOf(dept.getId()))) {
            eventDtos.addAll(map.get(BigInteger.valueOf(dept.getId())));
        }
    }

    private EventFilter prepareEventFilter() {
        EventFilter eventFilter = new EventFilter();
        eventFilter.setDepId(BigInteger.valueOf(dept.getId()));
        eventFilter.setDateLocal(Calendar.getInstance());
        logger.info("{}", eventFilter);
        return eventFilter;
    }

    private EventFilter prepareBeforeTodayEventFilter() {
        EventFilter eventFilter = new EventFilter();
        eventFilter.setDepId(BigInteger.valueOf(dept.getId()));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        eventFilter.setDateLocal(calendar);
        logger.info("{}", eventFilter);
        return eventFilter;
    }

    public String getTimeValue() {
        return timeValue;
    }

    @PostConstruct
    private void prepare() {
        String sid = getRequest().getParameter("id");
        dept = deptOldHolder.getDeptOldGuidMap().get(sid);
        if (null != dept) {
            title = dept.getName();
            process();
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    public DeptOld getDept() {
        return dept;
    }

    public void setDept(DeptOld dept) {
        this.dept = dept;
    }

    public List<EventDto> getEventDtos() {
        return eventDtos;
    }

    public void setEventDtos(List<EventDto> eventDtos) {
        this.eventDtos = eventDtos;
    }
}
