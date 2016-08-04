package me.sunyfusion.fuzion.hardware;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import me.sunyfusion.fuzion.Global;

/**
 * Created by jesse on 7/5/16.
 */
public class GPS {
    LocationManager locationManager;
    Context context;

    public GPS(Context c) {
        context = c;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Global.setEnabled("gpsLocation");
        if (Global.getInstance().gps != null) {
            Global.getInstance().gps.stopLocationUpdates();
        }
        Global.getInstance().gps = this;
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

    private void makeUseOfNewLocation(Location l) { /*
        latitude = l.getLatitude();
        longitude = l.getLongitude();
        gps_acc = l.getAccuracy();
        //Datestamp date = new Datestamp("date");
        Toast.makeText(Global.getContext(), "GPS Update, " + gps_acc, Toast.LENGTH_SHORT).show();
        System.out.println("GPS IS RUNNING " + gps_acc);
        if (Global.isTrackingActive() && Global.getConfig("id_key") != null && gps_acc <= 50f) {
            if (gps_tracker_lat != null && gps_tracker_long != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(gps_tracker_lat, latitude);
                contentValues.put(gps_tracker_long, longitude);
                //date.insertDate(contentValues);
                contentValues.put(Global.getConfig("id_key"), Global.getConfig("id_value"));
                //Run.checkDate();
                //Run.insert(contentValues);
                //Global.getDb().insert("tasksTable", null, contentValues);
            }
        } */
    }

    public static int getGpsFreq() {
        return GPS_FREQ;
    }

    public static void setGpsFreq(int f) {
        GPS_FREQ = f;
    }

    public void startLocationUpdates() throws SecurityException {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS.getGpsFreq(), 5, locationListener);
    }

    public void stopLocationUpdates() throws SecurityException {
        locationManager.removeUpdates(locationListener);
    }
}
