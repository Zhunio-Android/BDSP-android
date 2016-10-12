package me.sunyfusion.bdsp.tasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import me.sunyfusion.bdsp.db.BdspDB;
import me.sunyfusion.bdsp.state.Config;
import me.sunyfusion.bdsp.state.Global;

/**
 * Created by jesse on 8/1/16.
 */
public class UploadTask extends AsyncTask<Void, Void, ArrayList<JSONArray>> {
    public UploadTask() {
        super();
    }
    BdspDB db = Global.getDb();
    ArrayList<String> deleteQueue = new ArrayList<String>();

    @Override
    protected ArrayList<JSONArray> doInBackground(Void... voids) {
        ArrayList<JSONArray> jsonArrayList = new ArrayList<JSONArray>();
        HashSet<ArrayList<String>> runAndDateList = db.getColumns(Global.getConfig().getRun().getColumnName(),"date");
        for(ArrayList<String> s : runAndDateList) {
            Object[] str = s.toArray();
            Cursor c = db.queueAll(str);
            JSONArray j = new JSONArray();
            JSONObject jsonObject;
            if (c.getCount() == 0)   //Does not submit if database is empty
                return null;
            c.moveToNext();
            String[] cNames = c.getColumnNames();

            int cCount = c.getColumnCount();
            while (!c.isAfterLast()) {
                jsonObject = new JSONObject();
                for (int i = 0; i < cCount; i++) {
                    if (!cNames[i].equals("unique_table_id"))
                        try {
                            jsonObject.put(cNames[i], c.getString(i));
                        } catch (JSONException e) {
                            Log.d("BDSP", "JSONexception");
                        }
                }
                j.put(jsonObject);
                deleteQueue.add(c.getString(0));
                c.moveToNext();
            }
            JSONObject params = new JSONObject();
            try {
                params.put("data", j);
            } catch (JSONException e) {
                Log.d("BDSP", "JSONexception");
            }
            c.close();
            jsonArrayList.add(j);
        }
        return jsonArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<JSONArray> j) {
        super.onPostExecute(j);
        System.out.println(Config.SUBMIT_URL);
        for(JSONArray jsonArray : j) {
            Log.d("JSON ARRAY", jsonArray.toString());
            doHTTPpost(Config.SUBMIT_URL, jsonArray, null);
        }
    }

    /**
     * Creates and sends an HTTP post request to the server, which includes the image captured
     * from the getImage() method. Also adds the HTTP response from the server to the UI.
     */
    private boolean doHTTPpost(String url, JSONArray jsonParams, Uri imgUri) {
        final Context c = Global.getContext();
        AsyncHttpClient client = new AsyncHttpClient();
        final boolean status = false;
        StringEntity entity = null;
        //post test

        if (imgUri != null) {
            File myFile = new File(imgUri.getPath());
        }
        try {
            entity = new StringEntity(jsonParams.toString());
        } catch (Exception e) {
            System.out.println(e);
        }
        client.setBasicAuth("SUNY", "GreenTreeTables");
        client.setTimeout(20000);
        client.setMaxRetriesAndTimeout(0, 1);
        client.post(c, url, entity, "application/json", new FileAsyncHttpResponseHandler(c) {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                Toast toast = Toast.makeText(c, "Success " + statusCode, Toast.LENGTH_LONG);
                toast.show();
                Log.i("UPLOAD", "SUCCESS");
                AsyncTask<ArrayList<String>,Void,Void> emptyQueue = new EmptyDeleteQueueTask();
                emptyQueue.execute(deleteQueue);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, File errorResponse) {
                Toast toast = Toast.makeText(c, "Failed " + statusCode, Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
        return status;
    }
}