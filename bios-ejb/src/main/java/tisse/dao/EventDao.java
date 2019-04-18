package tisse.dao;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import tisse.filter.EventFilter;
import tisse.model.Event;
import tisse.model.EventType;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static tisse.util.DateUtils.clearDate;

@Stateless
public class EventDao extends BaseDao<Event> {

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

    public List<Event> findList(Calendar current, List<BigInteger> subjectIds) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(getGenericClass());
        final Root<Event> root = criteriaQuery.from(getGenericClass());
        criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isTrue(root.get(EVENT_ID).in(getEventTypeCodes())));
        predicates.add(criteriaBuilder.isTrue(root.get(SUBJECT_ID).in(subjectIds)));
        if (null != current) {
            Calendar dateLocalStart = (Calendar) current.clone();
            clearDate(dateLocalStart);
            long startTimeInMillis = dateLocalStart.getTimeInMillis();
            logger.info("{}", startTimeInMillis);
            startTimeInMillis = startTimeInMillis / 1000L;
            logger.info("{}", startTimeInMillis);

            Calendar dateLocalEnd = (Calendar) current.clone();
            clearDate(dateLocalEnd);
            dateLocalEnd.add(Calendar.DAY_OF_YEAR, 1);
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

        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(ID)));
        TypedQuery<Event> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public Event findLast() {
        try {
            final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(getGenericClass());
            final Root<Event> root = criteriaQuery.from(getGenericClass());
            criteriaQuery.select(root);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isTrue(root.get(EVENT_ID).in(getEventTypeCodes())));
            if (CollectionUtils.isNotEmpty(predicates)) {
                criteriaQuery.where(predicates.toArray(new Predicate[]{}));
            }

            criteriaQuery.orderBy(criteriaBuilder.desc(root.get(ID)));
            TypedQuery<Event> query = em.createQuery(criteriaQuery);
            query.setMaxResults(1);

            query.setFirstResult(0);
            List<Event> resultList = query.getResultList();
            if (CollectionUtils.isNotEmpty(resultList)) {
                return resultList.get(0);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;

    }

    public Event findPrevious(BigInteger objectId, Calendar current) {

        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(getGenericClass());
        final Root<Event> root = criteriaQuery.from(getGenericClass());
        criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isTrue(root.get(EVENT_ID).in(getEventTypeCodes())));
        predicates.add(criteriaBuilder.equal(root.get(OBJECT_ID), objectId));
        if (null != current) {
            Calendar dateLocalStart = (Calendar) current.clone();
            clearDate(dateLocalStart);
            dateLocalStart.add(Calendar.DAY_OF_YEAR, -1);
            long startTimeInMillis = dateLocalStart.getTimeInMillis();
            logger.info("{}", startTimeInMillis);
            startTimeInMillis = startTimeInMillis / 1000L;
            logger.info("{}", startTimeInMillis);

            Calendar dateLocalEnd = (Calendar) current.clone();
            clearDate(dateLocalEnd);
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

        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(ID)));
        TypedQuery<Event> query = em.createQuery(criteriaQuery);
        query.setMaxResults(1);

        query.setFirstResult(0);
        List<Event> resultList = query.getResultList();
        if (CollectionUtils.isNotEmpty(resultList)) {
            return resultList.get(0);
        }
        return null;

    }

    public List<Event> findPrevious(BigInteger objectId, BigInteger subjectId, Long id, Long previousInId, Calendar lastCalendar) {

        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(getGenericClass());
        final Root<Event> root = criteriaQuery.from(getGenericClass());
        criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isTrue(root.get(EVENT_ID).in(getEventTypeCodes())));
        predicates.add(criteriaBuilder.equal(root.get(OBJECT_ID), objectId));
        predicates.add(criteriaBuilder.equal(root.get(SUBJECT_ID), subjectId));
        predicates.add(criteriaBuilder.lessThan(root.get(ID), id));
        predicates.add(criteriaBuilder.ge(root.get(ID), previousInId));
        if (null != lastCalendar) {
            predicates.add(criteriaBuilder.ge(root.get(DATE), lastCalendar.getTimeInMillis()));
        }

        if (CollectionUtils.isNotEmpty(predicates)) {
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }

        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(ID)));
        TypedQuery<Event> query = em.createQuery(criteriaQuery);
        return query.getResultList();

    }

    public List<Event> findNext(BigInteger objectId, BigInteger subjectId, Long id, Long nextOutId) {

        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(getGenericClass());
        final Root<Event> root = criteriaQuery.from(getGenericClass());
        criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isTrue(root.get(EVENT_ID).in(EventType.getEventTypesOut())));
        predicates.add(criteriaBuilder.equal(root.get(OBJECT_ID), objectId));
        predicates.add(criteriaBuilder.equal(root.get(SUBJECT_ID), subjectId));
        predicates.add(criteriaBuilder.gt(root.get(ID), id));
        predicates.add(criteriaBuilder.le(root.get(ID), nextOutId));

        if (CollectionUtils.isNotEmpty(predicates)) {
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(ID)));
        TypedQuery<Event> query = em.createQuery(criteriaQuery);
        return query.getResultList();

    }

    public Event findPrevious(BigInteger objectId, Long id) {

        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(getGenericClass());
        final Root<Event> root = criteriaQuery.from(getGenericClass());
        criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isTrue(root.get(EVENT_ID).in(getEventTypeCodes())));
        predicates.add(criteriaBuilder.equal(root.get(OBJECT_ID), objectId));
        predicates.add(criteriaBuilder.lessThan(root.get(ID), id));

        if (CollectionUtils.isNotEmpty(predicates)) {
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }

        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(ID)));
        TypedQuery<Event> query = em.createQuery(criteriaQuery);
        query.setMaxResults(1);

        query.setFirstResult(0);
        List<Event> resultList = query.getResultList();
        if (CollectionUtils.isNotEmpty(resultList)) {
            return resultList.get(0);
        }
        return null;

    }

    public Event findPreviousIn(BigInteger objectId, BigInteger subjectId, Long id, Calendar lastCalendar) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(getGenericClass());
        final Root<Event> root = criteriaQuery.from(getGenericClass());
        criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isTrue(root.get(EVENT_ID).in(EventType.getEventTypesIn())));
        predicates.add(criteriaBuilder.equal(root.get(OBJECT_ID), objectId));
        predicates.add(criteriaBuilder.equal(root.get(SUBJECT_ID), subjectId));
        predicates.add(criteriaBuilder.lessThan(root.get(ID), id));
        if (null != lastCalendar) {
            predicates.add(criteriaBuilder.ge(root.get(DATE), lastCalendar.getTimeInMillis()));
        }

        if (CollectionUtils.isNotEmpty(predicates)) {
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }

        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(ID)));
        TypedQuery<Event> query = em.createQuery(criteriaQuery);
        query.setMaxResults(1);

        query.setFirstResult(0);
        List<Event> resultList = query.getResultList();
        if (CollectionUtils.isNotEmpty(resultList)) {
            return resultList.get(0);
        }
        return null;
    }

    public Event findNextOut(BigInteger objectId, BigInteger subjectId, Long id) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(getGenericClass());
        final Root<Event> root = criteriaQuery.from(getGenericClass());
        criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isTrue(root.get(EVENT_ID).in(EventType.getEventTypesOut())));
        predicates.add(criteriaBuilder.equal(root.get(OBJECT_ID), objectId));
        predicates.add(criteriaBuilder.equal(root.get(SUBJECT_ID), subjectId));
        predicates.add(criteriaBuilder.gt(root.get(ID), id));

        if (CollectionUtils.isNotEmpty(predicates)) {
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(ID)));
        TypedQuery<Event> query = em.createQuery(criteriaQuery);
        query.setMaxResults(1);

        query.setFirstResult(0);
        List<Event> resultList = query.getResultList();
        if (CollectionUtils.isNotEmpty(resultList)) {
            return resultList.get(0);
        }
        return null;

    }

    public Event findNextEvent(BigInteger objectId, BigInteger subjectId, Long id, Calendar lastCalendar) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(getGenericClass());
        final Root<Event> root = criteriaQuery.from(getGenericClass());
        criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isTrue(root.get(EVENT_ID).in(getEventTypeCodes())));
        predicates.add(criteriaBuilder.equal(root.get(OBJECT_ID), objectId));
        predicates.add(criteriaBuilder.equal(root.get(SUBJECT_ID), subjectId));
        predicates.add(criteriaBuilder.greaterThan(root.get(ID), id));
        if (null != lastCalendar) {
            predicates.add(criteriaBuilder.lessThan(root.get(DATE), lastCalendar.getTimeInMillis() / 1000));
        }
        if (CollectionUtils.isNotEmpty(predicates)) {
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(ID)));
        TypedQuery<Event> query = em.createQuery(criteriaQuery);
        query.setMaxResults(1);

        query.setFirstResult(0);
        List<Event> resultList = query.getResultList();
        if (CollectionUtils.isNotEmpty(resultList)) {
            return resultList.get(0);
        }
        return null;

    }

    public Event findNext(BigInteger objectId, Calendar current) {
        List<Integer> eventTypeCodes = getEventTypeCodes();

        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(getGenericClass());
        final Root<Event> root = criteriaQuery.from(getGenericClass());
        criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isTrue(root.get(EVENT_ID).in(eventTypeCodes)));
        predicates.add(criteriaBuilder.equal(root.get(OBJECT_ID), objectId));
        if (null != current) {
            Calendar dateLocalStart = (Calendar) current.clone();
            clearDate(dateLocalStart);
            dateLocalStart.add(Calendar.DAY_OF_YEAR, 1);
            long startTimeInMillis = dateLocalStart.getTimeInMillis();
            logger.info("{}", startTimeInMillis);
            startTimeInMillis = startTimeInMillis / 1000L;
            logger.info("{}", startTimeInMillis);

            Calendar dateLocalEnd = (Calendar) current.clone();
            clearDate(dateLocalEnd);
            dateLocalEnd.add(Calendar.DAY_OF_YEAR, 2);
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

        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(ID)));
        TypedQuery<Event> query = em.createQuery(criteriaQuery);
        query.setMaxResults(1);

        query.setFirstResult(0);
        List<Event> resultList = query.getResultList();
        if (CollectionUtils.isNotEmpty(resultList)) {
            return resultList.get(0);
        }
        return null;

    }

    public List<Event> list(EventFilter filter) {

        List<Integer> eventTypeCodes = getEventTypeCodes();

        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(getGenericClass());
        final Root<Event> root = criteriaQuery.from(getGenericClass());
        criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isTrue(root.get(EVENT_ID).in(eventTypeCodes)));
        if (null != filter.getSubjectId()) {
            predicates.add(criteriaBuilder.equal(root.get(SUBJECT_ID), filter.getSubjectId()));
        }
        if (null != filter.getObjectId()) {
            predicates.add(criteriaBuilder.equal(root.get(OBJECT_ID), filter.getObjectId()));
        }
        if (CollectionUtils.isNotEmpty(filter.getSubjectIds())) {
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
        TypedQuery<Event> query = em.createQuery(criteriaQuery);
        if (null != filter.getLimit()) {
            query.setMaxResults(filter.getLimit());
        }

        query.setFirstResult(0);
        return query.getResultList();
    }

    private List<Integer> getEventTypeCodes() {
        return Stream.of(EventType.values()).map(EventType::getCode).collect(Collectors.toList());
    }

}
