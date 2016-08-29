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
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import me.sunyfusion.bdsp.column.Tracker;
import me.sunyfusion.bdsp.db.BdspDB;
import me.sunyfusion.bdsp.notification.BdspNotification;
import me.sunyfusion.bdsp.state.Global;

public class TrackerService extends Service implements LocationListener {
    private final IBinder mBinder = new bdspBinder();
    BdspDB db;
    Tracker tracker;
    String timeStarted;
    Timer t;
    PowerManager.WakeLock wl;
    LocationManager locationManager;
    FileOutputStream outputStream;

    public TrackerService() {
        String filename = "myfile.txt";
        String string = "Hello world!";

        try {
            outputStream = openFileOutput(Environment.DIRECTORY_DOCUMENTS + "/test441.txt", Context.MODE_PRIVATE);

        } catch (Exception e) {
            e.printStackTrace();
        }
        locationManager = (LocationManager) Global.getContext().getSystemService(Context.LOCATION_SERVICE);
    }
    public void onLocationChanged(Location location) {
        makeUseOfNewLocation(location);
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    public void onProviderEnabled(String provider) {
    }
    public void onProviderDisabled(String provider) {
    }
    private void makeUseOfNewLocation(Location l) {
        System.out.println("NEW GPS FROM TRACKER");
    }

    public void startLocationUpdates() throws SecurityException {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
    }

    public void stopLocationUpdates() throws SecurityException {
        locationManager.removeUpdates(this);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tracker = new Tracker(getApplicationContext(),Global.getConfig());
        t = new Timer();
        PowerManager pm = (PowerManager) Global.getContext().getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TrackerService");
        wl.acquire();
        startLocationUpdates();
        db = new BdspDB(this);
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        d.setTimeZone(TimeZone.getDefault());
        timeStarted = d.format(new java.util.Date());
        Notification n = BdspNotification.notify(getApplicationContext(), timeStarted, 1);
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tracker.insertPoint();
                System.out.println("POINT LOGGED");
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                try {
                    outputStream.write(currentDateTimeString.getBytes());
                }
                catch(Exception e) {
                }
            }
        },0,1000);
        startForeground(1, n);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(TrackerService.this, timeStarted, Toast.LENGTH_SHORT).show();
        wl.release();
        t.cancel();
        stopLocationUpdates();
        try {
            outputStream.close();
        }
        catch(Exception e) {

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
        public TrackerService getService() {
            return TrackerService.this;
        }
    }


}
