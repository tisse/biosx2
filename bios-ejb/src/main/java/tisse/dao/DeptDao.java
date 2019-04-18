package tisse.dao;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import tisse.model.Dept;

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
public class DeptDao {

    @Inject
    private EntityManager em;
    @Inject
    private Logger logger;

    public Dept find(int id) {
        return em.find(Dept.class, id);
    }

    public List<Dept> list() {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Dept> criteriaQuery = criteriaBuilder.createQuery(Dept.class);
        final Root<Dept> root = criteriaQuery.from(Dept.class);
        criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(predicates)) {
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("name")));
        TypedQuery<Dept> query = em.createQuery(criteriaQuery);
        List<Dept> resultList = query.setFirstResult(0).getResultList();
        return resultList;

    }

}
