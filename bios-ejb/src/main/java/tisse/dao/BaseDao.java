package tisse.dao;

import tisse.model.BaseEntity;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class BaseDao<E extends BaseEntity> {

    @Inject
    protected EntityManager em;

    public E findById(Integer id) {
        return em.find(getGenericClass(), id);
    }

    public void save(E entity) {
        if (null != entity) {
            if (null != entity.getId()) {
                em.merge(entity);
            } else {
                em.persist(entity);
            }
        }
    }

    public List<E> list() {
        return em.createQuery("from " + getGenericName(), getGenericClass()).getResultList();
    }

    protected String getGenericName() {
        return getGenericClass().getSimpleName();
    }

    protected Class<E> getGenericClass() {
        return ((Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

}
