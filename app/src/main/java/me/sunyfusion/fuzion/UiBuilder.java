package me.sunyfusion.fuzion;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by jesse on 7/7/16.
 */
public class UiBuilder {

    public static PowerManager.WakeLock wakelock;

    public static void gpsTracker(String[] args, DatabaseHelper dbHelper, Context c) {
        PowerManager powerManager = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Fuzion");
        wakelock.acquire();
        if (args.length > 1 && args[2] != null) {
            GPSHelper.setGpsFreq(Integer.parseInt(args[2]));
            try {

            } catch (SecurityException e) {

            }
        }
        if (args.length > 3) {
            dbHelper.addColumn(DatabaseHelper.db, args[3], "TEXT");
            GPSHelper.gps_tracker_lat = args[3];
            GPSHelper.gps_tracker_long = args[4];
            dbHelper.addColumn(DatabaseHelper.db, args[4], "TEXT");
        }
        GPSHelper.sendGPS = true;
    }

}
