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

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import me.sunyfusion.bdsp.db.BdspDB;
import me.sunyfusion.bdsp.state.Config;
import me.sunyfusion.bdsp.state.Global;

/**
 * Created by jesse on 8/1/16.
 */
public class UploadTask extends AsyncTask<Void, Void, JSONArray> {
    public UploadTask() {
        super();
    }
    BdspDB db = Global.getDb();

    @Override
    protected JSONArray doInBackground(Void... voids) {
        JSONArray j = new JSONArray();
        JSONObject jsonObject;
        Cursor c = db.queueAll();
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
            db.deleteQueue.add(c.getString(0));
            c.moveToNext();
        }
        JSONObject params = new JSONObject();
        try {
            params.put("data", j);
        } catch (JSONException e) {
            Log.d("BDSP", "JSONexception");
        }
        c.close();
        return j;
    }

    @Override
    protected void onPostExecute(JSONArray j) {
        super.onPostExecute(j);
        doHTTPpost(Config.SUBMIT_URL, j, null);
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
                AsyncTask<Void,Void,Void> emptyQueue = new EmptyDeleteQueueTask();
                emptyQueue.execute();
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