package org.bd_sp.bdsp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;

import org.bd_sp.bdsp.state.BdspConfig;
import org.bd_sp.bdsp.tasks.UploadTask;

/**
 * Created by jesse on 4/19/16.
 */
public class NetUpdateReceiver extends BroadcastReceiver {

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

                Bundle bundle = intent.getExtras();


                Log.i("NET", "connected ");
                try {
                    String table = intent.getStringExtra("table");
                    AsyncTask<Void, Void, ArrayList<JSONArray>> doUpload = new UploadTask(context, BdspConfig.SUBMIT_URL, table);
                    doUpload.execute();
                }
                catch(Exception e) {
                    Log.d("UPLOADER", "Failed to upload");
                }

            }
        }
        else {
            if(netConnected) {
                netConnected = false;
                Log.i("NET", "not connected");
            }
        }
    }
}