package tisse.service.holder;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import tisse.dao.PersonDepDao;
import tisse.model.PersonDep;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class PersonDepHolder {

    @EJB
    private PersonDepDao dao;
    @Inject
    private Logger logger;

    private List<PersonDep> list;
    private Map<Integer, Integer> personDepMap;

    @PostConstruct
    private void init(){
        refresh();
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void refresh() {
        logger.info("refresh PersonDep list {}", DateFormatUtils.format(Calendar.getInstance(), "dd.MM.yyyy HH:mm:sss"));
        list = dao.list();
        personDepMap = list.stream().collect(Collectors.toMap(PersonDep::getWorkerId, PersonDep::getDepId));
    }

    public List<PersonDep> getList() {
        return list;
    }

    public Map<Integer, Integer> getPersonDepMap() {
        return personDepMap;
    }
}
