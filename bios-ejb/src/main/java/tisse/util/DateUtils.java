package tisse.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Calendar;

public class DateUtils {

    private DateUtils() {
    }

    public static void clearDate(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
    }


    public static String formatCurrent(){
        return DateFormatUtils.format(Calendar.getInstance(), "dd.MM.yyyy HH:mm:sss");
    }

}
