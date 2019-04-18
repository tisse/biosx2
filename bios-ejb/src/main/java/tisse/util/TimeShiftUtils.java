package tisse.util;

import tisse.dto.DepInfo;
import tisse.dto.TimeShift;

public class TimeShiftUtils {

    private TimeShiftUtils() {
    }

    public static TimeShift convert(DepInfo depInfo) {
        TimeShift timeShift = new TimeShift();
        timeShift.setTimeShift(depInfo.getTimeShift());
        timeShift.setDepGuId(depInfo.getUuid());
        timeShift.setName(depInfo.getName());
        return timeShift;
    }

    public static DepInfo convert(TimeShift timeShift) {
        DepInfo depInfo = new DepInfo();
        depInfo.setTimeShift(timeShift.getTimeShift());
        depInfo.setUuid(timeShift.getDepGuId());
        depInfo.setName(timeShift.getName());
        return depInfo;
    }

}
