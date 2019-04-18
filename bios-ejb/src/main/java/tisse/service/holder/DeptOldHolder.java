package tisse.service.holder;

import org.slf4j.Logger;
import tisse.dao.DeptOldDao;
import tisse.model.DeptOld;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static tisse.util.DateUtils.formatCurrent;

@Singleton
public class DeptOldHolder {

    @EJB
    private DeptOldDao dao;
    @Inject
    private Logger logger;

    private List<DeptOld> list;
    private Map<Integer, DeptOld> deptOldMap;
    private Map<String, DeptOld> deptOldGuidMap;

    @PostConstruct
    private void init(){
        refresh();
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void refresh() {
        logger.info("refresh DeptOld list {}", formatCurrent());
        list = dao.list();
        deptOldMap = list.stream().collect(Collectors.toMap(DeptOld::getId, Function.identity()));
        deptOldGuidMap = list.stream().collect(Collectors.toMap(DeptOld::getGuid1c, Function.identity()));
    }

    public List<DeptOld> getList() {
        return list;
    }

    public Map<Integer, DeptOld> getDeptOldMap() {
        return deptOldMap;
    }

    public Map<String, DeptOld> getDeptOldGuidMap() {
        return deptOldGuidMap;
    }
}
