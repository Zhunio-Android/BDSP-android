package me.sunyfusion.fuzion;

import android.content.Context;
import android.net.Uri;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.auth.AuthScope;

/**
 * Created by jesse on 3/15/16.
 */
public class HTTPFunc {
    private static final String BASE_URL = "http://sunyfusion.me";
    private static AsyncHttpClient client = new AsyncHttpClient();

    /**
     * Creates and sends an HTTP post request to the server, which includes the image captured
     * from the getImage() method. Also adds the HTTP response from the server to the UI.
     */
    public static boolean doHTTPpost(final Context c, String url, RequestParams params, Uri imgUri) {
        AsyncHttpClient client = new AsyncHttpClient();
        final boolean status = false;
        //post test

        if (imgUri != null) {
            File myFile = new File(imgUri.getPath());
            try {
                params.put("image", myFile);
            } catch (FileNotFoundException e) {
            }
        }

        client.post(url, params, new FileAsyncHttpResponseHandler(c) {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                Toast toast = Toast.makeText(c, "Success " + statusCode, Toast.LENGTH_LONG);
                toast.show();
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

    /**
     * creates and sends an Async HTTP GET request to our server, and adds the first line
     * of the server's response to the UI window.
     */
    public static void doHTTPget(final Context c, String getUrl) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getUrl, new FileAsyncHttpResponseHandler(c) {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                String b = "";
                try {
                    FileReader f = new FileReader(response);
                    BufferedReader r = new BufferedReader(f);
                    b = r.readLine();
                } catch (IOException e) {
                    b = e.getMessage();
                }
                Toast toast = Toast.makeText(c, "Success " + statusCode + " " + b, Toast.LENGTH_LONG);
                toast.show();
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
    }

}
