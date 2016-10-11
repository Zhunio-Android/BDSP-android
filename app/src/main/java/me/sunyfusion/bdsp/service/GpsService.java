package me.sunyfusion.bdsp.service;

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

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import me.sunyfusion.bdsp.column.Datestamp;
import me.sunyfusion.bdsp.column.Tracker;
import me.sunyfusion.bdsp.db.BdspDB;
import me.sunyfusion.bdsp.notification.BdspNotification;
import me.sunyfusion.bdsp.state.Config;
import me.sunyfusion.bdsp.state.Global;

public class GpsService extends Service implements LocationListener {
    private final IBinder mBinder = new bdspBinder();
    Tracker tracker;
    String timeStarted;
    Timer t;
    PowerManager.WakeLock wl;  // wakelock
    LocationManager locationManager;
    FileOutputStream outputStream;
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
        Config config = Global.getConfig();
        config.getLatitude().setLocation(l);
        config.getLongitude().setLocation(l);
    }

    public void startLocationUpdates() throws SecurityException {
        if(locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }
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
        if(Global.getInstance() != null) {
            tracker = new Tracker(Global.getContext(), Global.getConfig());
        }
        t = new Timer();
        getWakelock();
        startLocationUpdates();
        timeStarted = Datestamp.getDateString("yyyy-MM-dd HH:mm:ss");
        Notification n = BdspNotification.notify(getApplicationContext(), timeStarted, 1);
        if(Global.getConfig().isGpsTrackerEnabled()) {
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (location != null) {
                        tracker.insertPoint(location);
                        System.out.println(location.getAccuracy());
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
        try {
            outputStream.close();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
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
        PowerManager pm = (PowerManager) Global.getContext().getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GpsService");
        wl.acquire();
    }
}
