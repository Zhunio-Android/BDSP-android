package me.sunyfusion.fuzion.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import me.sunyfusion.fuzion.tasks.UploadTask;

/**
 * Created by jesse on 4/19/16.
 */
public class UpdateReceiver extends BroadcastReceiver {

    public static boolean netConnected = false;

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
                try {
                    AsyncTask<Void, Void, JSONArray> doUpload = new UploadTask();
                    doUpload.execute();
                }
                catch(Exception e) {
                    Log.d("UPLOADER", "THAT DIDN'T WORK");
                }

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