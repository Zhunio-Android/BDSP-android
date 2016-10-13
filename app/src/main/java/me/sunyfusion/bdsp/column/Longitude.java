package me.sunyfusion.bdsp.column;

import android.content.ContentValues;
import android.content.Context;
import android.location.Location;

import me.sunyfusion.bdsp.db.BdspDB;

/**
 * Created by deisingj1 on 8/8/2016.
 */
public class Longitude extends Column {
    Location location;
    public Longitude(Context c, String s, BdspDB db) {
        super(c,s, db);
    }
    @Override
    public void insertValue(ContentValues cv) {
        if(location != null) {
            cv.put(getName(), location.getLongitude());
        }
    }
    public void setLocation(Location l) {
        location = l;
    }
}
