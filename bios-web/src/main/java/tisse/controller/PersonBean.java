package tisse.controller;


import org.slf4j.Logger;
import tisse.model.Person;
import tisse.service.holder.PersonHolder;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.List;

@RequestScoped
@ManagedBean
public class PersonBean {

    @Inject
    private Logger logger;

    @Inject
    private PersonHolder personHolder;

    private List<Person> people;

    @PostConstruct
    public void process(){
        logger.info("process");
        people = personHolder.getList();
    }

    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }
}
