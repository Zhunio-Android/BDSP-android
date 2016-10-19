package me.sunyfusion.bdsp;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by jesse on 10/13/16.
 */

public class Utils {

    public static String getDateString(String format) {
        SimpleDateFormat d = new SimpleDateFormat(format);
        d.setTimeZone(TimeZone.getDefault());
        String cdt = d.format(new java.util.Date());
        return cdt;
    }
}
