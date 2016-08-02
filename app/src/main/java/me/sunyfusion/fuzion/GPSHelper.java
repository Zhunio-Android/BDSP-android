package me.sunyfusion.fuzion;

import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by jesse on 7/5/16.
 */
public class GPSHelper {
    LocationManager locationManager;
    Context context;

    public GPSHelper(Context c, String latCol, String longCol) {
        context = c;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        latitude_col = latCol;
        longitude_col = longCol;
        Global.getDbHelper().addColumn(latitude_col, "TEXT");
        Global.getDbHelper().addColumn(longitude_col, "TEXT");
        Global.setEnabled("gpsLocation");
        if (Global.getInstance().gpsHelper != null) {
            Global.getInstance().gpsHelper.stopLocationUpdates();
        }
        Global.getInstance().gpsHelper = this;
        startLocationUpdates();
    }

    private static int GPS_FREQ = 5000;
    static double latitude = -1;
    static double longitude = -1;
    double gps_acc = 1000;
    String gps_tracker_lat, gps_tracker_long, latitude_col, longitude_col;

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            makeUseOfNewLocation(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    private void makeUseOfNewLocation(Location l) {
        latitude = l.getLatitude();
        longitude = l.getLongitude();
        gps_acc = l.getAccuracy();
        DateObject date = new DateObject("date");
        Toast.makeText(Global.getContext(), "GPS Update, " + gps_acc, Toast.LENGTH_SHORT).show();
        System.out.println("GPS IS RUNNING " + gps_acc);
        if (Global.isTrackingActive() && Global.getConfig("id_key") != null && gps_acc <= 50f) {
            if (gps_tracker_lat != null && gps_tracker_long != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(gps_tracker_lat, latitude);
                contentValues.put(gps_tracker_long, longitude);
                date.insertDate(contentValues);
                contentValues.put(Global.getConfig("id_key"), Global.getConfig("id_value"));
                Run.checkDate();
                Run.insert(contentValues);
                Global.getDb().insert("tasksTable", null, contentValues);
            }
        }
    }

    public static int getGpsFreq() {
        return GPS_FREQ;
    }

    public static void setGpsFreq(int f) {
        GPS_FREQ = f;
    }

    public void startLocationUpdates() throws SecurityException {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPSHelper.getGpsFreq(), 5, locationListener);
    }

    public void stopLocationUpdates() throws SecurityException {
        locationManager.removeUpdates(locationListener);
    }
}
