package tisse.controller;


import org.slf4j.Logger;
import tisse.service.Csv1CWriter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

@RequestScoped
@ManagedBean
public class Csv1CBean {

    @Inject
    private Logger logger;

    @Inject
    private Csv1CWriter csv1CWriter;

    public void process(){
        logger.info("process");
        csv1CWriter.process(null);
    }

}
