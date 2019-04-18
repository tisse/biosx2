package tisse.service.holder;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import tisse.dao.JobDao;
import tisse.model.Job;

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
public class JobHolder {

    @EJB
    private JobDao dao;
    @Inject
    private Logger logger;

    private List<Job> list;
    private Map<Integer, Job> map;


    @PostConstruct
    private void init() {
        refresh();
    }

    public List<Job> getList() {
        return list;
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void refresh() {
        logger.info("refresh Job list {}", DateFormatUtils.format(Calendar.getInstance(), "dd.MM.yyyy HH:mm:sss"));
        list = dao.list();
        map = list.stream().collect(Collectors.toMap(Job::getId, Function.identity()));
    }

    public Map<Integer, Job> getMap() {
        return map;
    }

}
