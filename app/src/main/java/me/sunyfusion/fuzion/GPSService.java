package me.sunyfusion.fuzion;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GPSService extends Service {
    public GPSService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Global g = Global.getInstance();
        UiBuilder.getWakelock(this);
        GPSHelper gpsHelper = Global.getInstance().gpsHelper;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Global.getInstance().gpsHelper.stopLocationUpdates();
        UiBuilder.wakelock.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
