package org.bd_sp.bdsp.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import org.bd_sp.bdsp.BdspRow;
import org.bd_sp.bdsp.R;
import org.bd_sp.bdsp.Utils;
import org.bd_sp.bdsp.adapter.FieldAdapter;
import org.bd_sp.bdsp.fields.Camera;
import org.bd_sp.bdsp.fields.Field;
import org.bd_sp.bdsp.receiver.NetUpdateReceiver;
import org.bd_sp.bdsp.service.GpsService;
import org.bd_sp.bdsp.state.BdspConfig;
import org.bd_sp.bdsp.state.Global;
import org.bd_sp.bdsp.tasks.UploadTask;

// Tim wore rubber bands on his wrist
// For each item on his to-do list
// But the more he forgot
// The bigger it got
// And now it's a big rubber fist

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /** Tag for logging */
    private static final String TAG = "MainActivity";

    // CONSTANTS
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;                        //used by GPS permissions dialog
    private BdspConfig bdspConfig;                                                                  //stores config information
    ArrayList<Field> fields;                                                                        //list of fields as read from config file

    /**
     * Runs on startup, creates the layout when the activity is created.
     * This is essentially the "main" method.
     *
     * @param savedInstanceState contains previous state (if saved) on entry
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.getInstance().init(this);

        try { bdspConfig = new BdspConfig(this, this.getAssets().open("buildApp.txt")); }           //check to make sure there is a valid
        catch(IOException e) {                                                                      //configuration
            System.out.println("Error in configuration");
            Toast.makeText(this,"ERROR IN PROJECT CONFIGURATION, EXITING", Toast.LENGTH_LONG).show();
            finishAffinity();
        }

        fields = bdspConfig.getFields();                                                            //get list of fields from configuration

        setContentView(R.layout.activity_main);                                                     //start setting up UI

        // Set up ToolBar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);                                //if you want more information
        setSupportActionBar(myToolbar);                                                             //about how this works
        getSupportActionBar().setLogo(R.mipmap.logo);                                               //look up the android docs for
        getSupportActionBar().setDisplayUseLogoEnabled(true);                                       //recyclerview

        // Set up RecyclerView
        RecyclerView mRecyclerView;
        RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;
        mRecyclerView = (RecyclerView) findViewById(R.id.uniques_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(fields.size());
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FieldAdapter(fields);
        mRecyclerView.setAdapter(mAdapter);                                                         //end UI setup
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.action_mode_close_button:
                stopService(new Intent(getApplicationContext(),GpsService.class));
                BdspRow.getInstance().clear();
                BdspRow.clearId();
                finishAffinity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        if(BdspRow.getId().isEmpty()) {
            showIdEntry(bdspConfig.getIdKey());
            getSupportActionBar().setSubtitle(bdspConfig.getIdKey() + " : ");
        }

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        NetUpdateReceiver.netConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,GpsService.class));
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
        switch(requestCode) {                                                                       //on receiving activity result
            case Camera.REQUEST_IMAGE_CAPTURE:                                                      //if Camera
                super.onActivityResult(requestCode, resultCode, data);
                if (resultCode == RESULT_OK) {
                    if(getView(Camera.photoLabel) != null) {
                        ((ImageView) getView(Camera.photoLabel).findViewById(Camera.valueId)).setImageURI(Camera.photoURI); //Save Image URI to row
                    }
                }


                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        /**
         * Switch based on id of the view that was clicked.
         * Using this method over having individual onClick
         * listeners for each object means that all onClick
         * actions are within one method, easier to debug
         */
        switch (view.getId()) {                                                             //when click received
            case R.id.submit:                                                               //if submit
                Utils.checkDate(this);                                                      //check to see if run needs to be reset
                Utils.getPhotoList(this);                                                   //get list of all photos
                BdspRow.getInstance().prepare(getApplicationContext());                     //prepare row for submission
                ContentValues cv = BdspRow.getInstance().getRow();                          //get object that will be submitted to phone database
                try {
                    Global.getDb().insert(cv);                                              //insert CV into database
                } catch (SQLiteException e) {
                    Log.d("Database", "ERROR inserting: " + e.toString());
                }
                if (NetUpdateReceiver.netConnected) {                                       //if the device is connected to the network
                    try {
                        new UploadTask(this, BdspConfig.SUBMIT_URL, bdspConfig.table).execute();              //do upload
                    } catch (Exception e) {
                        Log.d("UPLOADER", "THAT DIDN'T WORK");
                    }
                }
                BdspRow.getInstance().clear();                                              //reinitialize the row, clear all fields
                clearUIFields();                                                            //clear fields in UI
                break;
            default:
                System.out.println(view.getId());
                break;
        }
    }

    /**
     * Returns the corresponding view for a given label in the form
     Use this to get the EditText box for a given labeled field
     so you can write to it.
     * @param label
     * @return the view that corresponds with the label
     */
    public View getView(String label) {
        for(Field f : fields) {
            if(f.getLabel().equals(label)) {
                return f.getView();
            }
        }
        System.out.println("GETVIEW : Did not find");
        return null;
    }

    /**
     * Clears all editable fields within the fields view object
     */
    private void clearUIFields() {
        for(Field f : fields) {
            f.clearField();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    System.out.println("They said yes!");
                    startService(new Intent(MainActivity.this, GpsService.class));
                } else {
                    System.out.println("They said no!");
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    /**
     * Creates and shows the dialog for ID entry on startup of the application
     * This method is called by onCreate after the configuration file has been read
     *
     * @param id_key The name of the id field, displayed in the dialog and
     *               used as the primary key when submitting a row into the database
     */
    private void showIdEntry(final String id_key) {
        final EditText idTxt = new EditText(this);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.alert_login);
        adb.setMessage("Enter " + id_key);
        adb.setView(idTxt);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (idTxt.getText().toString().equals("")) {
                    showIdEntry(id_key);
                } else {
                    String id = idTxt.getText().toString().replace(' ', '_');
                    BdspRow.setId(id);
                    SharedPreferences prefs = getSharedPreferences("BDSP", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("id", id);
                    editor.commit();
                    getSupportActionBar().setSubtitle(bdspConfig.getIdKey() + " : " + id);
                }
            }
        });
        adb.setCancelable(false);
        adb.show();
    }
}
