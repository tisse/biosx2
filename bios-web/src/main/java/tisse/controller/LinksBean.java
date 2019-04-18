package tisse.controller;

import org.slf4j.Logger;
import tisse.model.DeptOld;
import tisse.service.DepService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import java.util.List;

@ManagedBean
@SessionScoped
public class LinksBean extends BaseBean {

    @Inject
    private Logger logger;
    @Inject
    private DepService depService;
    private List<DeptOld> deptOlds;

    @PostConstruct
    private void prepare() {
        refresh();
    }

    public void refresh() {
        depService.refresh();
        deptOlds = depService.getDeptOlds();
        infoMessage("Список обновлен");
    }


    @Override
    public String getTitle() {
        return "Links";
    }

    public List<DeptOld> getDeptOlds() {
        return deptOlds;
    }

    public void setDeptOlds(List<DeptOld> deptOlds) {
        this.deptOlds = deptOlds;
    }
}
