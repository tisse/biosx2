package tisse.dao;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import tisse.model.PersonDep;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class PersonDepDao {

    @Inject
    private EntityManager em;
    @Inject
    private Logger logger;

    public PersonDepDao find(int id) {
        return em.find(PersonDepDao.class, id);
    }

    public List<PersonDep> list() {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<PersonDep> criteriaQuery = criteriaBuilder.createQuery(PersonDep.class);
        final Root<PersonDep> root = criteriaQuery.from(PersonDep.class);
        criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(predicates)) {
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("workerId")));
        TypedQuery<PersonDep> query = em.createQuery(criteriaQuery);
        List<PersonDep> resultList = query.setFirstResult(0).getResultList();
        return resultList;
    }

}
