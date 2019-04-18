package tisse.rest;


import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import tisse.dao.*;
import tisse.dto.EventDto;
import tisse.filter.EventFilter;
import tisse.model.Dept;
import tisse.model.DeptOld;
import tisse.model.Event;
import tisse.service.EventService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Path("/")
@Produces("application/json; charset=UTF-8")
public class TestResource {

    @Inject
    private Logger logger;
    @Inject
    private PersonDao personDao;
    @Inject
    private FirmDao firmDao;
    @Inject
    private EventDao eventDao;
    @Inject
    private DeptOldDao deptDao;
    @Inject
    private EventService eventService;

    @GET
    @Path("/people")
    public Response people() {
        return Response.ok(personDao.list()).build();
    }

    @GET
    @Path("/service/event")
    public Response eventService(@QueryParam("subjectId") BigInteger subjectId,
                                 @QueryParam("objectId") BigInteger objectId,
                                 @QueryParam("depId") BigInteger depId,
                                 @QueryParam("limit") Integer limit) {
        EventFilter eventFilter = new EventFilter();
        eventFilter.setSubjectId(subjectId);
        eventFilter.setObjectId(objectId);
        eventFilter.setDepId(depId);
        Calendar dateLocalStart = Calendar.getInstance();
        dateLocalStart.add(Calendar.DAY_OF_YEAR, -1);
        logger.info("{}", DateFormatUtils.format(dateLocalStart, "dd.MM.yyyy HH:mm:sss"));
        eventFilter.setDateLocal(dateLocalStart);
        eventFilter.setLimit(limit);
        Map<BigInteger, List<EventDto>> list = eventService.process(eventFilter);
        return Response.ok(list).build();
    }

    @GET
    @Path("/firm")
    public Response firm() {
        return Response.ok(firmDao.list()).build();
    }

    @GET
    @Path("/dept")
    public Response dept() {
        List<DeptOld> list = deptDao.list();
        logger.info("{}", list.size());
        Map<Integer, DeptOld> deptMap = list.stream().collect(Collectors.toMap(DeptOld::getId, Function.identity()));
        return Response.ok(deptMap).build();
    }

    @GET
    @Path("/event")
    public Response event(@QueryParam("subjectId") BigInteger subjectId, @QueryParam("objectId") BigInteger objectId) {
        EventFilter eventFilter = new EventFilter();
        eventFilter.setSubjectId(subjectId);
        eventFilter.setObjectId(objectId);
        Calendar dateLocalStart = Calendar.getInstance();
        dateLocalStart.add(Calendar.DAY_OF_YEAR, -1);
        logger.info("{}", DateFormatUtils.format(dateLocalStart, "dd.MM.yyyy HH:mm:sss"));
        eventFilter.setDateLocal(dateLocalStart);
        List<Event> list = eventDao.list(eventFilter);
        Map<BigInteger, List<Event>> collect = list.stream().collect(Collectors.groupingBy(Event::getFirm));
        return Response.ok(collect).build();
    }

}
