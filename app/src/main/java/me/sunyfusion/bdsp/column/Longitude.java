package me.sunyfusion.bdsp.column;

import android.content.ContentValues;
import android.content.Context;
import android.location.Location;

/**
 * Created by deisingj1 on 8/8/2016.
 */
public class Longitude extends Column {
    Location location;
    public Longitude(Context c, String s) {
        super(c,s);
    }
    @Override
    public void insertValue(ContentValues cv) {
        if(location != null) {
            cv.put(getColumnName(), location.getLongitude());
        }
    }
    public void setLocation(Location l) {
        location = l;
    }
}
