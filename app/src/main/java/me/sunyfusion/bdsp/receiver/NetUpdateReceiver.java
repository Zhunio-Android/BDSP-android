package me.sunyfusion.bdsp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;

import me.sunyfusion.bdsp.state.BdspConfig;
import me.sunyfusion.bdsp.tasks.UploadTask;

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
                Log.i("NET", "connected ");
                try {
                    AsyncTask<Void, Void, ArrayList<JSONArray>> doUpload = new UploadTask(context, BdspConfig.SUBMIT_URL);
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