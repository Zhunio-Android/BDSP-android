package me.sunyfusion.fuzion;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

public class bdspService extends Service {
    private final IBinder mBinder = new bdspBinder();
    private final Global g = Global.getInstance();

    public PowerManager.WakeLock wakelock;
    ArrayList<Unique> uniques;

    public void getWakelock(Context c) {
        PowerManager powerManager = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BDSP");
        wakelock.acquire();
    }

    public bdspService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Global.getContext() != null) {
            getWakelock(Global.getContext());
        }
        if (Global.isEnabled("gpsLocation") || Global.isEnabled("gpsTracking")) {
            Global.getInstance().gpsHelper.startLocationUpdates();
        }
        Notification n = new NotificationCompat.Builder(this).setContentTitle("TEST")
                .setTicker("THis test")
                .setContentText("YO")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark).build();
        startForeground(1, n);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wakelock.release();
        Global.getInstance().gpsHelper.stopLocationUpdates();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("service", "onBind");
        return mBinder;
    }

    public class bdspBinder extends Binder {
        bdspService getService() {
            return bdspService.this;
        }
    }


}
