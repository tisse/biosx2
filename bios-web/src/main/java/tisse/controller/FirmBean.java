package tisse.controller;


import org.slf4j.Logger;
import tisse.dao.FirmDao;
import tisse.model.Firm;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.List;

@RequestScoped
@ManagedBean
public class FirmBean {

    @Inject
    private Logger logger;

    @Inject
    private FirmDao firmDao;
    private List<Firm> firms;

    @PostConstruct
    public void process(){
        logger.info("process");
        firms = firmDao.list();
    }

    public List<Firm> getFirms() {
        return firms;
    }

    public void setFirms(List<Firm> firms) {
        this.firms = firms;
    }
}
