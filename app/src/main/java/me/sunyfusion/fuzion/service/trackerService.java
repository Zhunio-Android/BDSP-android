package me.sunyfusion.fuzion.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import me.sunyfusion.fuzion.column.Tracker;
import me.sunyfusion.fuzion.db.BdspDB;
import me.sunyfusion.fuzion.notification.bdspNotification;
import me.sunyfusion.fuzion.state.Global;

public class trackerService extends Service {
    private final IBinder mBinder = new bdspBinder();
    BdspDB db;
    Tracker tracker;
    String timeStarted;
    public trackerService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = new BdspDB(this);
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        d.setTimeZone(TimeZone.getDefault());
        timeStarted = d.format(new java.util.Date());
        Notification n = bdspNotification.notify(getApplicationContext(), timeStarted, 1);
        tracker = new Tracker(getApplicationContext(),Global.getConfig());
        startForeground(1, n);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(trackerService.this, timeStarted, Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("service", "onBind");
        return mBinder;
    }

    public class bdspBinder extends Binder {
        public trackerService getService() {
            return trackerService.this;
        }
    }


}
