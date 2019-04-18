package tisse.service.holder;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import tisse.model.PersonDep;
import tisse.service.manager.DepTrackManager;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static tisse.util.Constants.SHORT_PATTERN;

@Singleton
public class DepTrackHolder {

    @Inject
    private Logger logger;

    private Map<String, List<PersonDep>> map;

    @EJB
    private DepTrackManager depTrackManager;

    @PostConstruct
    private void init() {
        map = new HashMap<>();
    }

    public List<PersonDep> getPersonDeps(Calendar calendar) {
        String format = DateFormatUtils.format(calendar, SHORT_PATTERN);
        if (map.containsKey(format)) {
            return map.get(format);
        } else {
            List<PersonDep> personDeps = depTrackManager.loadList(calendar);
            map.put(format, personDeps);
            return personDeps;
        }
    }

    public PersonDep find(Calendar calendar, Integer workerId) {
        return getPersonDeps(calendar).stream().filter(personDep -> personDep.getWorkerId().compareTo(workerId) == 0).findFirst().orElse(null);
    }

    public List<PersonDep> getPersonDeps(Calendar calendar, List<Integer> depIds) {
        return getPersonDeps(calendar).stream()
                .filter(pd -> depIds.contains(pd.getDepId()))
                .collect(Collectors.toList());
    }


    public List<BigInteger> getWorkerIds(Calendar calendar, List<Integer> depIds) {
        return getPersonDeps(calendar).stream()
                .filter(pd -> depIds.contains(pd.getDepId()))
                .map(PersonDep::getWorkerId)
                .map(BigInteger::valueOf)
                .collect(Collectors.toList());
    }

    public List<BigInteger> getWorkerIds(Calendar calendar) {
        return getPersonDeps(calendar).stream()
                .map(PersonDep::getWorkerId)
                .map(BigInteger::valueOf)
                .collect(Collectors.toList());
    }

}
