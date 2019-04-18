package tisse.service;

import tisse.model.DeptOld;
import tisse.service.holder.DeptOldHolder;
import tisse.service.holder.TimeShiftHolder;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class DepService {

    @EJB
    private DeptOldHolder deptOldHolder;
    @EJB
    private TimeShiftHolder timeShiftHolder;

    private List<DeptOld> deptOlds;

    @PostConstruct
    private void init() {
        Set<String> guids = timeShiftHolder.getShiftMap().keySet();
        deptOlds = new ArrayList<>();
        deptOldHolder.getDeptOldGuidMap().forEach((guid, deptOld) -> {
            if (guids.contains(guid)) {
                deptOlds.add(deptOld);
            }
        });
        deptOlds = deptOlds.stream().sorted(Comparator.comparing(DeptOld::getName)).collect(Collectors.toList());
    }

    public void refresh() {
        init();
    }

    public List<DeptOld> getDeptOlds() {
        return deptOlds;
    }
}
