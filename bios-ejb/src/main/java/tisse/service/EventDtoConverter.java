package tisse.service;

import org.apache.commons.lang3.time.DateFormatUtils;
import tisse.dto.EventDto;
import tisse.model.Dept;
import tisse.model.Event;
import tisse.model.Person;
import tisse.model.PersonDep;
import tisse.service.holder.DepTrackHolder;
import tisse.service.holder.DeptHolder;
import tisse.service.holder.PersonHolder;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static tisse.util.Constants.LONG_PATTERN;
import static tisse.util.Constants.SHORT_PATTERN;

@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class EventDtoConverter {

    @EJB
    private DepTrackHolder depTrackHolder;
    @Inject
    private DeptHolder deptHolder;
    @Inject
    private PersonHolder personHolder;

    public EventDto convert(Event event) {
        Map<Integer, Person> personMap = personHolder.getPersonMap();

        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setCardId(event.getCardId());
        eventDto.setCurTime(event.getCurTime());
        eventDto.setDate(event.getDate());
        eventDto.setDateLocal(event.getDateLocal());
        eventDto.setFirm(event.getFirm());
        eventDto.setEventId(event.getEventId());
        eventDto.setInternal(event.getInternal());
        eventDto.setObjectId(event.getObjectId());
        eventDto.setSubjectId(event.getSubjectId());

        eventDto.setEventType(event.getEventType());
        eventDto.setEventKind(event.getEventTypeValue());
        eventDto.setDateLocalValue(event.getEventDateLocal().format(DateTimeFormatter.ofPattern(LONG_PATTERN)));
        eventDto.setDateValue(event.getEventDate().format(DateTimeFormatter.ofPattern(LONG_PATTERN)));

        eventDto.setDateShortValue(event.getEventDate().format(DateTimeFormatter.ofPattern(SHORT_PATTERN)));
        eventDto.setCurTimeValue(DateFormatUtils.format(event.getCurTime(), LONG_PATTERN));

        PersonDep personDep = depTrackHolder.find(event.getCurTime(), event.getSubjectId().intValue());
        if (null != personDep) {
            eventDto.setDept(BigInteger.valueOf(personDep.getDepId()));
            Dept d = deptHolder.getMap().get(eventDto.getDept().intValue());
            eventDto.setDepName(d.getName());
        }

        Person person = personMap.get(event.getSubjectId().intValue());
        if (person != null) {
            eventDto.setFirstName(person.getFirstName());
            eventDto.setLastName(person.getLastName());
        }

        return eventDto;
    }


}
