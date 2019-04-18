package tisse.service.holder;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import tisse.dao.PersonDao;
import tisse.model.Person;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
@TransactionAttribute(TransactionAttributeType.NEVER)
public class PersonHolder {

    @EJB
    private PersonDao dao;
    @Inject
    private Logger logger;

    private List<Person> list;
    private Map<Integer, Person> personMap;
    private Map<String, List<Person>> personTabNumMap;

    @PostConstruct
    private void init() {
        refresh();
    }

    public List<Person> getList() {
        return list;
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void refresh() {
        logger.info("refresh Person list {}", DateFormatUtils.format(Calendar.getInstance(), "dd.MM.yyyy HH:mm:sss"));
        list = dao.list();
        personMap = list.stream().collect(Collectors.toMap(Person::getId, Function.identity()));
        personTabNumMap = list.stream().collect(Collectors.groupingBy(Person::getTabNum));
    }

    public Map<Integer, Person> getPersonMap() {
        return personMap;
    }

    public Map<String, List<Person>> getPersonTabNumMap() {
        return personTabNumMap;
    }
}
