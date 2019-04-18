package tisse.dao;

import tisse.model.ParamType;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;


@Stateless
public class ParamTypeDao extends BaseDao<ParamType> {

    public List<ParamType> test(){
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ParamType> criteriaQuery = criteriaBuilder.createQuery(getGenericClass());
        final Root<ParamType> root = criteriaQuery.from(getGenericClass());
        criteriaQuery.select(root);

        Predicate predicate = criteriaBuilder.conjunction();
        criteriaQuery.where(predicate);

        List<ParamType> resultList = em.createQuery(criteriaQuery).setFirstResult(0).setMaxResults(10).getResultList();
        return resultList;
    }

}
