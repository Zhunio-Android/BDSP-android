package me.sunyfusion.fuzion;

import android.content.ContentValues;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by jesse on 5/2/16.
 */
public class DateObject {
    String dateColumnName;

    public DateObject(String name) {
        dateColumnName = name;
    }

    public void insertDate(ContentValues v) {
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        d.setTimeZone(TimeZone.getDefault());
        String cdt = d.format(new Date());
        v.put(dateColumnName, cdt);
    }

    public static String getDate() {
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        d.setTimeZone(TimeZone.getDefault());
        String cdt = d.format(new Date());
        return cdt;
    }
}
