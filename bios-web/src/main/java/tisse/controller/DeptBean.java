package tisse.controller;

import org.slf4j.Logger;
import tisse.dao.DeptDao;
import tisse.model.Dept;
import tisse.model.DeptOld;
import tisse.service.DepService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.List;

@RequestScoped
@ManagedBean
public class DeptBean {

    @Inject
    private Logger logger;

    @Inject
    private DepService depService;
    private List<DeptOld> depts;

    public List<DeptOld> getDepts() {
        return depts;
    }

    @PostConstruct
    private void init() {
        depts = depService.getDeptOlds();
    }
}
