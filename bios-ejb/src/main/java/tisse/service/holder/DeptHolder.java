package tisse.service.holder;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import tisse.dao.DeptDao;
import tisse.model.Dept;

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
public class DeptHolder {

    @EJB
    private DeptDao dao;
    @Inject
    private Logger logger;

    private List<Dept> list;
    private Map<Integer, Dept> map;

    @PostConstruct
    private void init() {
        refresh();
    }

    public List<Dept> getList() {
        return list;
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void refresh() {
        logger.info("refresh Dept list {}", DateFormatUtils.format(Calendar.getInstance(), "dd.MM.yyyy HH:mm:sss"));
        list = dao.list();
        map = list.stream().collect(Collectors.toMap(Dept::getId, Function.identity()));
    }


    public Map<Integer, Dept> getMap() {
        return map;
    }
}
