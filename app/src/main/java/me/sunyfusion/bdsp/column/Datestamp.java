package me.sunyfusion.bdsp.column;

import android.content.ContentValues;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by jesse on 5/2/16.
 */
public class Datestamp extends Column {

    public Datestamp(Context c, String name) {
        super(c,name);
    }

    @Override
    public void insertValue(ContentValues v) {
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        d.setTimeZone(TimeZone.getDefault());
        String cdt = d.format(new java.util.Date());
        v.put(getColumnName(), cdt);
        System.out.println("subclass insertValue date");
    }

    public static String getDateString(String format) {
        SimpleDateFormat d = new SimpleDateFormat(format);
        d.setTimeZone(TimeZone.getDefault());
        String cdt = d.format(new java.util.Date());
        return cdt;
    }
}
