
package me.sunyfusion.fuzion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import android.view.ViewGroup.LayoutParams;

import cz.msebera.android.httpclient.Header;

//Comment *JESSE* //
public class MainActivity extends Activity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    Uri imgUri;
    private static LinearLayout l;

    EditText mText;

    private static ViewGroup layout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    /**
     * Runs on startup, creates the layout when the activity is created.
     * This is essentially the "main" method.
     *
     * @param savedInstanceState restores previous state on entry
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = (ViewGroup) findViewById(R.id.layout);

        //     l = new LinearLayout(this);
        //     l.setOrientation(LinearLayout.VERTICAL);
        //     final mTextView t,u;
        // setContentView(l);

        //Insert dynamic layout object into view.
   /*     try {
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

        Button button = new Button(this);
        button.setText("DO REQUESTS");
        l.addView(button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doHTTPget();
                doHTTPpost();
            }
        });
        mText = new EditText(this);
        l.addView(mText);
*/
        buildSubmit();
        dispatch();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://me.sunyfusion.fuzion/http/host/path")
        );
        AppIndex.AppIndexApi.start(client2, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://me.sunyfusion.fuzion/http/host/path")
        );
        AppIndex.AppIndexApi.end(client2, viewAction);
        client2.disconnect();
    }

    /**
     * Wrapper class to create a constructor for TextView that allows text to be set at the
     * time of creation of the object.
     */
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

    /**
     * Receives all intents returned by activities when returning to this activity.
     * Right now, this only processes the intent returned by the getImage() method
     * below
     *
     * @param requestCode integer that identifies the type of activity that the intent belongs to
     * @param resultCode  status code returned by the exiting activity
     * @param data        the intent that is being returned by the exiting activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Img saved to:\n" +
                    data.getData(), Toast.LENGTH_LONG).show();
            imgUri = data.getData();
        }
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

    /**
     * Creates a file in the application's directory on the device, assigns a timestamped file
     * name, creates a new Image Capture intent, and starts it. the overridden method
     * "onActivityResult" handles the data returned by the camera intent.
     *
     * @return void
     */
    private void getImage() {
        File f = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File s = new File(f.getPath() + File.separator + timeStamp + ".jpg");
        Uri uri = Uri.fromFile(s);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Creates and sends an HTTP post request to the server, which includes the image captured
     * from the getImage() method. Also adds the HTTP response from the server to the UI.
     */
    private void doHTTPpost() {
        AsyncHttpClient client = new AsyncHttpClient();
        //post test
        RequestParams params = new RequestParams();
        params.put("test", mText.getText());
        if (imgUri != null) {
            File myFile = new File(imgUri.getPath());
            try {
                params.put("image", myFile);
            } catch (FileNotFoundException e) {
            }
        }
        client.post("http://sunyfusion.me/ft_test/index.php", params, new FileAsyncHttpResponseHandler(getApplicationContext()) {

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
                    while ((b = r.readLine()) != null) {
                        l.addView(new mTextView(getApplicationContext(), b));
                    }
                } catch (IOException e) {
                }
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

    /**
     * creates and sends an Async HTTP GET request to our server, and adds the first line
     * of the server's response to the UI window.
     */
    private void doHTTPget() {
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
                } catch (IOException e) {
                }
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

    public void dispatch() {
        String Type;
        String Name;
        Scanner infile = null;

        try {
            infile = new Scanner(this.getAssets().open("buildApp.txt"));   // scans File
        } catch (Exception e) {
            e.printStackTrace();
        }
        ReadFromInput readFile = new ReadFromInput(infile);

        do {
            try {
                readFile.getNextLine();
                readFile.ReadLineCollectInfo();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Type = readFile.getType();

            switch (Type) {
                case "camera":
                    if (readFile.getAnswer() == 1) {
                        buildCamera();
                        System.out.println(Type + " " + readFile.getAnswer());
                    }
                    break;

                case "gpsLoc":
                    if (readFile.getAnswer() == 1) {
                        buildGpsLoc();
                        System.out.println(Type + " " + readFile.getAnswer());
                    }
                    break;

                case "gpsTracker":
                    if (readFile.getAnswer() == 1) {
                        buildGpsTracker();
                        System.out.println(Type + " " + readFile.getAnswer());
                    }
                    break;

                case "unique":
                    Name = readFile.getUnigueName();
                    buildUniqueName(Name);
                    System.out.println(Type + " " + readFile.getUnigueName());
                    break;

            }
        }
        while (!Type.equals("endFile"));
    }

    public void buildCamera() {
        // build button
        // add column to SQLite table

        Button cameraButton = new Button(this);
        cameraButton.setText("Camera");
        cameraButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getImage();
            }
        });
           layout.addView(cameraButton);

    }

    public void buildGpsLoc() {
        // build button
        // add column to SQLite table

        Button buildGPSLocButton = new Button(this);
        buildGPSLocButton.setText("GPS Location");
        buildGPSLocButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        buildGPSLocButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // action
            }
        });

        layout.addView(buildGPSLocButton);
        //  l.addView(gpsLocButton);
    }

    public void buildGpsTracker() {
        // build or activate GpsTracker
    }

    public void buildUniqueName(String name) {
        // build unique button
        // add column to SQLite table
       // Button uniqueButton = (Button) findViewById(R.id.inputButtons);
        Button uniqueButton = new Button(this);
        uniqueButton.setText(name);
        uniqueButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        uniqueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // action
            }
        });

        layout.addView(uniqueButton);
    }

    public void buildSubmit() {


        // Button uniqueButton = (Button) findViewById(R.id.inputButtons);
        Button submitButton = new Button(this);
        submitButton.setText("Submit Data");
       // submitButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
       //         LayoutParams.WRAP_CONTENT));

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // action
            }
        });

        layout.addView(submitButton);
    }
}


/*
final Button button = (Button) findViewById(R.id.button_id);
button.setOnClickListener(new View.OnClickListener() {
public void onClick(View v) {
*/