package me.sunyfusion.fuzion;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        final mTextView t,u;
        setContentView(l);
        //Insert dynamic layout object into view.
        try {
            InputStreamReader f = new InputStreamReader(this.getAssets().open("test.txt"));
            BufferedReader r = new BufferedReader(f);
            t = new mTextView(this, r.readLine());
            l.addView(t);
        }
        //If asset does not exist, just put something there
        catch(IOException e) {
            u = new mTextView(this, e.getMessage());
            l.addView(u);
        }
        //TODO move these get and post tests to some other method/class
        //TODO outside of the UI oncreate method
        //get Test
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://sunyfusion.me", new FileAsyncHttpResponseHandler(getApplicationContext()) {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                Toast toast = Toast.makeText(getApplicationContext(), "Success " + statusCode, Toast.LENGTH_LONG);
                toast.show();
                try {
                    FileReader f = new FileReader(response);
                    BufferedReader r = new BufferedReader(f);
                    String b = r.readLine();
                    String c = "";
                    //while((b = r.readLine()) != null) {
                        l.addView(new mTextView(getApplicationContext(), b));
                    //}
                 }
                catch(IOException e){}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, File errorResponse) {
                Toast toast = Toast.makeText(getApplicationContext(), "Failed" + statusCode, Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
        //post test
        RequestParams params = new RequestParams();
        params.put("test", "working");
        client.post("http://sunyfusion.me/submit.php", params, new FileAsyncHttpResponseHandler(getApplicationContext()) {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                Toast toast = Toast.makeText(getApplicationContext(), "Success " + statusCode, Toast.LENGTH_LONG);
                toast.show();
                try {
                    FileReader f = new FileReader(response);
                    BufferedReader r = new BufferedReader(f);
                    String b = "";
                    String c = "";
                    while((b = r.readLine()) != null) {
                        l.addView(new mTextView(getApplicationContext(), b));
                    }
                }
                catch(IOException e){}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, File errorResponse) {
                Toast toast = Toast.makeText(getApplicationContext(), "Failed" + statusCode, Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    class mTextView extends TextView {
        public mTextView(Context c, String t) {
            super(c);
            this.setText(t);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
