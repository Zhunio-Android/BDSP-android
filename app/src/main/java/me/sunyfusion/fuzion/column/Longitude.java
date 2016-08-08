package me.sunyfusion.fuzion.column;

import android.content.ContentValues;
import android.content.Context;

import me.sunyfusion.fuzion.hardware.GPS;

/**
 * Created by deisingj1 on 8/8/2016.
 */
public class Longitude extends Column {
    GPS gps;
    public Longitude(Context c, String s, GPS gps) {
        super(c,s);
        this.gps = gps;
    }
    @Override
    public void insertValue(ContentValues cv) {
        cv.put(getColumnName(), gps.longitude);
    }
}
