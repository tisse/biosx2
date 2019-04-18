package tisse.service.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import tisse.model.PersonDep;
import tisse.service.holder.PersonDepHolder;
import tisse.util.DepTrackUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class DepTrackManager {

    @Inject
    private Logger logger;

    @Inject
    private PersonDepHolder personDepHolder;

    public List<BigInteger> load(Calendar calendar) {
        boolean exists = DepTrackUtils.exists(calendar);
        if (exists) {
            Gson gson = new Gson();
            try {
                Type itemsListType = new TypeToken<List<PersonDep>>() {
                }.getType();
                List<PersonDep> personDep = gson.fromJson(new FileReader(DepTrackUtils.getFileName(calendar)), itemsListType);
                return personDep.stream()
                        .map(PersonDep::getWorkerId)
                        .map(BigInteger::valueOf)
                        .collect(Collectors.toList());
            } catch (FileNotFoundException e) {
                logger.error("", e);
            }
        }
        return new ArrayList<>();
    }

    public List<BigInteger> load(Calendar calendar, List<Integer> depIds) {
        boolean exists = DepTrackUtils.exists(calendar);
        if (exists) {
            Gson gson = new Gson();
            try {
                Type itemsListType = new TypeToken<List<PersonDep>>() {
                }.getType();
                List<PersonDep> personDep = gson.fromJson(new FileReader(DepTrackUtils.getFileName(calendar)), itemsListType);
                return personDep.stream()
                        .filter(pd -> depIds.contains(pd.getDepId()))
                        .map(PersonDep::getWorkerId)
                        .map(BigInteger::valueOf)
                        .collect(Collectors.toList());
            } catch (FileNotFoundException e) {
                logger.error("", e);
            }
        }
        return new ArrayList<>();
    }

    public List<PersonDep> loadList(Calendar calendar) {
        if (DepTrackUtils.exists(calendar)) {
            Gson gson = new Gson();
            try {
                Type itemsListType = new TypeToken<List<PersonDep>>() {
                }.getType();
                return gson.fromJson(new FileReader(DepTrackUtils.getFileName(calendar)), itemsListType);
            } catch (FileNotFoundException e) {
                logger.error("", e);
            }
        }
        return personDepHolder.getList();
    }


}
