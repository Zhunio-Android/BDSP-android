package me.sunyfusion.bdsp.column;

import android.content.ContentValues;
import android.content.Context;

import me.sunyfusion.bdsp.hardware.GPS;

/**
 * Created by deisingj1 on 8/8/2016.
 */
public class Latitude extends Column {
    GPS gps;
    public Latitude(Context c, String s, GPS gps) {
        super(c,s);
        this.gps = gps;
    }
    @Override
    public void insertValue(ContentValues cv) {
        cv.put(getColumnName(), gps.latitude);
    }
}
