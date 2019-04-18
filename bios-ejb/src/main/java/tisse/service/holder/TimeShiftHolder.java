package tisse.service.holder;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import tisse.dto.TimeShift;
import tisse.service.TimeShiftLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class TimeShiftHolder {

    private Map<Integer, List<TimeShift>> timeShiftMap;
    private List<TimeShift> timeShifts;
    private Map<String, TimeShift> shiftMap;

    @Inject
    private Logger logger;
    @EJB
    private TimeShiftLoader timeShiftLoader;

    @PostConstruct
    private void init() {
        refresh();
    }

    public void refresh() {
        timeShiftMap = new HashMap<>();
        timeShifts = new ArrayList<>();

        String path = System.getProperty("bios.timeshift.file");

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            timeShifts = timeShiftLoader.load(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            logger.error("", e);
        }

        fillMap();
    }

    public void update(List<TimeShift> timeShiftList) {
        timeShifts = new ArrayList<>(timeShiftList);
        timeShiftMap = new HashMap<>();
        fillMap();
        logger.info("updated");
    }

    private void fillMap() {
        if (CollectionUtils.isNotEmpty(timeShifts)) {
            timeShiftMap = timeShifts.stream().collect(Collectors.groupingBy(TimeShift::getTimeShift));
            shiftMap = timeShifts.stream().collect(Collectors.toMap(TimeShift::getDepGuId, Function.identity(), (o, o2) -> o2));
        }
    }

    public List<TimeShift> get(Integer shift) {
        return timeShiftMap.get(shift);
    }

    public List<TimeShift> getTimeShifts() {
        return timeShifts;
    }

    public Map<String, TimeShift> getShiftMap() {
        return shiftMap;
    }
}
