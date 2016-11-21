package me.sunyfusion.bdsp.service;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import me.sunyfusion.bdsp.BdspRow;
import me.sunyfusion.bdsp.Utils;
import me.sunyfusion.bdsp.exception.LocationManagerNullException;
import me.sunyfusion.bdsp.notification.BdspNotification;
import me.sunyfusion.bdsp.state.Global;

public class GpsService extends Service implements LocationListener {
    private final IBinder mBinder = new bdspBinder();
    String timeStarted;
    Timer t;
    PowerManager.WakeLock wl;  // wakelock
    LocationManager locationManager;
    Location location;

    public GpsService() {
    }

    //-------------------------------------------------------------------
    // Methods that implement LocationListener
    //-------------------------------------------------------------------
    public void onLocationChanged(Location l) {
        makeUseOfNewLocation(l);
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    public void onProviderEnabled(String provider) {
    }
    public void onProviderDisabled(String provider) {
    }
    private void makeUseOfNewLocation(Location l) {
        location = l;
    }

    public void startLocationUpdates() throws SecurityException,LocationManagerNullException {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        catch(NullPointerException e) {
            System.out.println(e.getMessage());
        }
        if(locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }
        else throw new LocationManagerNullException();
    }

    public void stopLocationUpdates() throws SecurityException {
        if(locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    //-------------------------------------------------------------------
    // Methods that extend Service
    //-------------------------------------------------------------------
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        t = new Timer();
        getWakelock();
        try {
            startLocationUpdates();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
        timeStarted = Utils.getDateString("yyyy-MM-dd HH:mm:ss");
        Notification n = BdspNotification.notify(getApplicationContext(), timeStarted, 1);
        if(true) {
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(Utils.getDateString("HH:mm:ss").contains("00:00:0")){
                        stopService(new Intent(getApplicationContext(),GpsService.class));
                        BdspRow.getInstance().clear();
                        BdspRow.clearId();
                        ((Activity) Global.getContext()).finishAffinity();
                    }
                    if (location != null) {
                        BdspRow.getInstance().addToKml(location);
                        BdspRow.getInstance().attachLocation(location);
                    }
                }
            }, 0, 1000);
        }
        startForeground(1, n);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(GpsService.this, timeStarted, Toast.LENGTH_SHORT).show();
        wl.release();
        t.cancel();
        stopLocationUpdates();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("service", "onBind");
        return mBinder;
    }

    public class bdspBinder extends Binder {
        public GpsService getService() {
            return GpsService.this;
        }
    }

    //-------------------------------------------------------------------
    // Everything else
    //-------------------------------------------------------------------
    private void getWakelock() {
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GpsService");
        wl.acquire();
    }
}
