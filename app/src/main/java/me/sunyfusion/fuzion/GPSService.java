package me.sunyfusion.fuzion;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GPSService extends Service {
    public GPSService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GPSHelper gpsHelper = Global.getInstance().gpsHelper;
        Global g = Global.getInstance();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Global.getInstance().gpsHelper.stopLocationUpdates();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
