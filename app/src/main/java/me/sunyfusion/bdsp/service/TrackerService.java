package me.sunyfusion.bdsp.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import me.sunyfusion.bdsp.column.Tracker;
import me.sunyfusion.bdsp.db.BdspDB;
import me.sunyfusion.bdsp.notification.BdspNotification;
import me.sunyfusion.bdsp.state.Global;

public class TrackerService extends Service {
    private final IBinder mBinder = new bdspBinder();
    BdspDB db;
    Tracker tracker;
    String timeStarted;
    public TrackerService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = new BdspDB(this);
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        d.setTimeZone(TimeZone.getDefault());
        timeStarted = d.format(new java.util.Date());
        Notification n = BdspNotification.notify(getApplicationContext(), timeStarted, 1);
        tracker = new Tracker(getApplicationContext(),Global.getConfig());
        startForeground(1, n);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(TrackerService.this, timeStarted, Toast.LENGTH_SHORT).show();
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
