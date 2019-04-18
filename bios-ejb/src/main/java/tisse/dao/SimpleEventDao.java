package tisse.dao;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import tisse.filter.EventFilter;
import tisse.model.Event;
import tisse.model.EventType;
import tisse.model.SimpleEvent;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static tisse.util.DateUtils.clearDate;

@Stateless
public class SimpleEventDao extends BaseDao<SimpleEvent> {

    private static final String ID = "id";
    private static final String SUBJECT_ID = "subjectId";
    private static final String DATE_LOCAL = "dateLocal";
    private static final String DATE = "date";
    private static final String EVENT_ID = "eventId";
    private static final String OBJECT_ID = "objectId";


    @Inject
    private Logger logger;
    @Inject
    private Integer cutoff;


    private List<Integer> getEventTypeCodes() {
        return Stream.of(EventType.values()).map(EventType::getCode).collect(Collectors.toList());
    }

    public List<SimpleEvent> list(EventFilter filter) {

        List<Integer> eventTypeCodes = getEventTypeCodes();

        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<SimpleEvent> criteriaQuery = criteriaBuilder.createQuery(getGenericClass());
        final Root<SimpleEvent> root = criteriaQuery.from(getGenericClass());
        criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isTrue(root.get(EVENT_ID).in(eventTypeCodes)));
        if (null != filter.getSubjectId()) {
            predicates.add(criteriaBuilder.equal(root.get(SUBJECT_ID), filter.getSubjectId()));
        }
        if (null != filter.getObjectId()) {
            predicates.add(criteriaBuilder.equal(root.get(OBJECT_ID), filter.getObjectId()));
        }
        if (CollectionUtils.isNotEmpty(filter.getSubjectIds())){
            predicates.add(criteriaBuilder.isTrue(root.get(SUBJECT_ID).in(filter.getSubjectIds())));
        }
        if (null != filter.getDateLocal()) {
            Calendar dateLocalStart = (Calendar) filter.getDateLocal().clone();
            clearDate(dateLocalStart);
            dateLocalStart.add(Calendar.HOUR_OF_DAY, cutoff);
            logger.info("{}", DateFormatUtils.format(dateLocalStart, "dd.MM.yyyy HH:mm:ss"));
            long startTimeInMillis = dateLocalStart.getTimeInMillis();
            logger.info("{}", startTimeInMillis);
            startTimeInMillis = startTimeInMillis / 1000L;
            logger.info("{}", startTimeInMillis);

            Calendar dateLocalEnd = (Calendar) filter.getDateLocal().clone();
            clearDate(dateLocalEnd);
            dateLocalEnd.add(Calendar.DAY_OF_YEAR, 1);
            dateLocalEnd.add(Calendar.HOUR_OF_DAY, cutoff);
            logger.info("{}", DateFormatUtils.format(dateLocalEnd, "dd.MM.yyyy HH:mm:ss"));
            long endTimeInMillis = dateLocalEnd.getTimeInMillis();
            logger.info("{}", endTimeInMillis);
            endTimeInMillis = endTimeInMillis / 1000L;
            logger.info("{}", endTimeInMillis);

            Predicate between = criteriaBuilder.between(root.get(DATE), startTimeInMillis, endTimeInMillis);
            predicates.add(between);
        }

        if (CollectionUtils.isNotEmpty(predicates)) {
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(ID)));
        TypedQuery<SimpleEvent> query = em.createQuery(criteriaQuery);
        if (null != filter.getLimit()) {
            query.setMaxResults(filter.getLimit());
        }

        query.setFirstResult(0);
        return query.getResultList();
    }


}
