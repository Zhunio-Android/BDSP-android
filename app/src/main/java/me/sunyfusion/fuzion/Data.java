package me.sunyfusion.fuzion;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by jesse on 8/1/16.
 */
public class Data {

    public static void enterUniquesToDatabase() {
        ViewGroup v = (ViewGroup) ((Activity) Global.getContext()).findViewById(R.id.my_recycler_view);
        ContentValues values = Global.getValues();
        for (int i = 0; i < v.getChildCount(); i++) {
            ViewGroup line = (ViewGroup) v.getChildAt(i);
            TextView colName = (TextView) line.getChildAt(0);
            EditText colValue = (EditText) line.getChildAt(1);
            values.put(colName.getText().toString(), colValue.getText().toString());
            colValue.getText().clear();
        }
    }

    public static void submit() {
        ContentValues values = Global.getValues();
        values.put(Global.getConfig("id_key"), Global.getConfig("id_value"));

        double latitude = GPSHelper.latitude;
        double longitude = GPSHelper.longitude;

        Run.checkDate();          // Compares dates for persistent variables
        Run.insert(values);   // Inserts persistent into ContentValue object
        Run.increment();                          // Increments persistent variable
        if (Global.isEnabled("addLocationToSubmission")) {
            values.put("longitude", longitude);
            values.put("latitude", latitude);
        }
        Data.enterUniquesToDatabase();
        Global.getInstance().date.insertDate(values);
        try {
            Global.getDb().insert("tasksTable", null, values);
        } catch (SQLiteException e) {
            Log.d("Database", "ERROR inserting: " + e.toString());
        }
        resetButtonsAfterSave();
        if (UpdateReceiver.netConnected) {
            try {
                AsyncTask<Void, Void, JSONArray> doUpload = new Upload();
                doUpload.execute();
            } catch (Exception e) {
                Log.d("UPLOADER", "THAT DIDN'T WORK");
            }
        }
    }

    public static void resetButtonsAfterSave() {
        Global.clearValues();

        if (Global.isEnabled("camera")) {
            //camera.setImageResource(android.R.drawable.ic_menu_camera);
        }

        if (Global.isEnabled("gpsLocation")) {
            //gpsLocation.setText("GPS LOCATION");
        }
    }
}

