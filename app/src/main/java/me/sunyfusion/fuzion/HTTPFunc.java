package me.sunyfusion.fuzion;

import android.content.Context;
import android.net.Uri;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by jesse on 3/15/16.
 */
public class HTTPFunc {
    public HTTPFunc(Context context){
        c = context;
    }
    private Context c;
    private static final String BASE_URL = "http://sunyfusion.me";
    private static AsyncHttpClient client = new AsyncHttpClient();

    /**
     * Creates and sends an HTTP post request to the server, which includes the image captured
     * from the getImage() method. Also adds the HTTP response from the server to the UI.
     */
    public boolean doHTTPpost(String url, JSONArray jsonParams, Uri imgUri) {
        AsyncHttpClient client = new AsyncHttpClient();
        final boolean status = false;
        StringEntity entity = null;
        //post test

        if (imgUri != null) {
            File myFile = new File(imgUri.getPath());

        }
        try {
            entity = new StringEntity(jsonParams.toString());
        }
        catch(Exception e){
            System.out.println(e);
        }
        client.setBasicAuth("SUNY","GreenTreeTables");
        client.post(c, url, entity, "application/json", new FileAsyncHttpResponseHandler(c) {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                //Toast toast = Toast.makeText(c, "Success " + statusCode, Toast.LENGTH_LONG);
                //toast.show();
                Toast toast = Toast.makeText(c, "Success" + statusCode, Toast.LENGTH_LONG);
                toast.show();
                Log.i("UPLOAD", "SUCCESS");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, File errorResponse) {
                Toast toast = Toast.makeText(c, "Failed" + statusCode, Toast.LENGTH_LONG);
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
