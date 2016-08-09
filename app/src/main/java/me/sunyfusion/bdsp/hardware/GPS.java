package me.sunyfusion.bdsp.hardware;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import me.sunyfusion.bdsp.column.Tracker;
import me.sunyfusion.bdsp.state.Global;

/**
 * Created by jesse on 7/5/16.
 */
public class GPS {
    LocationManager locationManager;
    Context context;
    Tracker tracker;

    public GPS(Context c) {
        context = c;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        startLocationUpdates();
    }

    private static int GPS_FREQ = 1000;
    public double latitude = -1;
    public double longitude = -1;
    double gps_acc = 1000;

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
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
        Toast.makeText(Global.getContext(), "GPS Update, " + gps_acc, Toast.LENGTH_SHORT).show();
        System.out.println("GPS IS RUNNING " + gps_acc);
        if (tracker != null && Global.getConfig().getIdValue() != null && gps_acc <= 1000f) {
            tracker.insertPoint();
        }
    }

    public void startLocationUpdates() throws SecurityException {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_FREQ, 0, locationListener);
    }

    public void stopLocationUpdates() throws SecurityException {
        locationManager.removeUpdates(locationListener);
    }
    public void bindTracker(Tracker t) {
        tracker = t;
    }
}
