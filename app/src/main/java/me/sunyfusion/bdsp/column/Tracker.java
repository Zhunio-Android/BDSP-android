package me.sunyfusion.bdsp.column;

import android.content.ContentValues;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import me.sunyfusion.bdsp.db.BdspDB;
import me.sunyfusion.bdsp.hardware.GPS;
import me.sunyfusion.bdsp.state.Config;
import me.sunyfusion.bdsp.state.Global;

/**
 * Created by deisingj1 on 8/8/2016.
 */
public class Tracker {
    GPS gps;
    Context c;
    Config config;
    BdspDB db = Global.getDb();

    public Tracker(Context c, Config config) {
        this.gps = config.getGps();
        gps.bindTracker(this);
        this.config = config;
        db.addColumn("latTrack", "TEXT");
        db.addColumn("longTrack", "TEXT");
    }

    public void insertPoint() {
        ContentValues cv = new ContentValues();
        cv.put("latTrack", gps.latitude);
        cv.put("longTrack", gps.longitude);
        config.getId().insertValue(cv);
        cv.put("date", getDateString());
        config.getRun().checkDate();
        config.getRun().insertValueNoInc(cv);
        db.insert(cv);
    }
    private String getDateString() {
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        d.setTimeZone(TimeZone.getDefault());
        String cdt = d.format(new java.util.Date());
        return cdt;
    }

}
