package me.sunyfusion.fuzion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by jesse on 4/19/16.
 */
public class UpdateReceiver extends BroadcastReceiver {

    protected static boolean netConnected = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
        if (isConnected) {
            if(!netConnected) {
                netConnected = true;
                Log.i("NET", "connected " + isConnected);
               // MainActivity.upload();
            }
        }
        else {
            if(netConnected) {
                netConnected = false;
                Log.i("NET", "not connected " + isConnected);
            }
        }
    }
}