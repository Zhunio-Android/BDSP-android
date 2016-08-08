package me.sunyfusion.fuzion.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import me.sunyfusion.fuzion.notification.bdspNotification;

public class bdspService extends Service {
    private final IBinder mBinder = new bdspBinder();

    public bdspService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification n = bdspNotification.notify(getApplicationContext(), "THIS IS A TEST", 1);
        startForeground(1, n);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("service", "onBind");
        return mBinder;
    }

    public class bdspBinder extends Binder {
        public bdspService getService() {
            return bdspService.this;
        }
    }


}
