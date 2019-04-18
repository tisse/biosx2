package tisse.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

public class DepTrackUtils {

    private DepTrackUtils() {
    }

    public static String getFileName(Calendar calendar) {
        String biosDepDir = System.getProperty("bios.dep.path");
        String format = DateFormatUtils.format(calendar, "yyyy-MM-dd");
        return biosDepDir.concat(format).concat(".json");
    }

    public static boolean exists(Calendar calendar){
        Path path = Paths.get(getFileName(calendar));
        return path.toFile().exists();
    }

}
