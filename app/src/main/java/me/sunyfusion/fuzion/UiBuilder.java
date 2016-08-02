package me.sunyfusion.fuzion;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by jesse on 7/7/16.
 */
public class UiBuilder {

    public static PowerManager.WakeLock wakelock;

    public static void getWakelock(Context c) {
        PowerManager.WakeLock wakelock;
        PowerManager powerManager = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BDSP");
        wakelock.acquire();
    }

    public static void gpsTracker(String[] args, DatabaseHelper dbHelper, Context c) {

        if (Global.getInstance().gpsHelper == null) {
            new GPSHelper(Global.getContext(), "latitude", "longitude");
        }
        GPSHelper gpsHelper = Global.getInstance().gpsHelper;
        if (args.length > 1 && args[2] != null) {
            gpsHelper.setGpsFreq(Integer.parseInt(args[2]));
            try {

            } catch (SecurityException e) {

            }
        }
        if (args.length > 3) {
            dbHelper.addColumn(args[3], "TEXT");
            gpsHelper.gps_tracker_lat = args[3];
            gpsHelper.gps_tracker_long = args[4];
            dbHelper.addColumn(args[4], "TEXT");
        }
        Global.setTracking(true);
    }

}
