package tisse.service;

import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import tisse.dao.PersonDepDao;
import tisse.model.PersonDep;

import javax.ejb.*;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;

import static tisse.util.DepTrackUtils.getFileName;

@Singleton
@TransactionAttribute(TransactionAttributeType.NEVER)
public class DepTracker {

    @Inject
    private Logger logger;
    @EJB
    private PersonDepDao dao;

    @Schedule(hour = "23")
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void track() {
        List<PersonDep> list = dao.list();
        String s = new GsonBuilder().setPrettyPrinting().create().toJson(list);
        String format = getFileName(Calendar.getInstance());
        try {
            Files.write(Paths.get(format), s.getBytes());
        } catch (IOException e) {
            logger.error("{}", e);
        }
    }

}
