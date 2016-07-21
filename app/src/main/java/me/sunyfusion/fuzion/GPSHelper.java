package me.sunyfusion.fuzion;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by jesse on 7/5/16.
 */
public class GPSHelper {
    LocationManager locationManager;
    Context context;

    public GPSHelper(Context c) {
        context = c;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    private static int GPS_FREQ = 1000;
    static double latitude = -1;
    static double longitude = -1;
    double gps_acc = 1000;
    static String gps_tracker_lat;
    static String gps_tracker_long;
    static boolean sendGPS = false;

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
        DateHelper date = new DateHelper("date");
        SharedPreferences sharedPref = context.getSharedPreferences("BDSP", Context.MODE_PRIVATE);


        if (sendGPS && MainActivity.id_key != null && gps_acc <= 300f) {
            if (gps_tracker_lat != null && gps_tracker_long != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(gps_tracker_lat, latitude);
                contentValues.put(gps_tracker_long, longitude);
                date.insertDate(contentValues);
                contentValues.put(MainActivity.id_key, MainActivity.id_value);
                Run.checkDate(context, sharedPref);
                Run.insert(context, sharedPref, contentValues);
                MainActivity.dbHelper.getCurrentDB().insert("tasksTable", null, contentValues);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPSHelper.getGpsFreq(), 0, locationListener);
    }
}
