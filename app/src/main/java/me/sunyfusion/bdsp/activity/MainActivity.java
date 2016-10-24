package me.sunyfusion.bdsp.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;

import me.sunyfusion.bdsp.BdspRow;
import me.sunyfusion.bdsp.R;
import me.sunyfusion.bdsp.Utils;
import me.sunyfusion.bdsp.adapter.UniqueAdapter;
import me.sunyfusion.bdsp.db.BdspDB;
import me.sunyfusion.bdsp.receiver.NetUpdateReceiver;
import me.sunyfusion.bdsp.service.GpsService;
import me.sunyfusion.bdsp.state.Config;
import me.sunyfusion.bdsp.state.Global;
import me.sunyfusion.bdsp.tasks.UploadTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // CONSTANTS
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    MenuItem cameraMenu;
    MenuItem gpsMenu;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Config config;
    private BdspDB db;

    /**
     * Runs on startup, creates the layout when the activity is created.
     * This is essentially the "main" method.
     *
     * @param savedInstanceState contains previous state (if saved) on entry
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build());*/
        final View.OnClickListener self = this;
        super.onCreate(savedInstanceState);
        Global.getInstance().init(this);
        config = new Config(this);  // Stores all of the config info from build app.txt
        db = Global.getDb();
        ArrayList<String> uniques = config.getUniques();
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setLogo(R.mipmap.logo);
        Button b = new Button(this);
        b.setText("Out of Service");
        b.setBackgroundColor(Color.RED);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                self.onClick(findViewById(R.id.submit));
                stopService(new Intent(getApplicationContext(),GpsService.class));
                BdspRow.getInstance().clear();
                BdspRow.clearId();
                finishAffinity();
            }
        });
        getSupportActionBar().setCustomView(b, new ActionBar.LayoutParams(Gravity.RIGHT));
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setSubtitle(config.getIdKey() + " : ");
        mRecyclerView = (RecyclerView) findViewById(R.id.uniques_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(uniques.size());

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new UniqueAdapter(uniques);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);

        gpsMenu = menu.getItem(1);
        /*
        if (config.isLocationEnabled()) {
            gpsMenu.setVisible(true);
        }
        */
        cameraMenu = menu.getItem(0);
        /*if (config.isPhotoEnabled()) {
            cameraMenu.setVisible(true);
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_mode_close_button) {
            stopService(new Intent(getApplicationContext(),GpsService.class));
            BdspRow.getInstance().clear();
            BdspRow.clearId();
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        if(BdspRow.getId().isEmpty()) {
            showIdEntry(config.getIdKey());
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Img saved successfully", Toast.LENGTH_LONG).show();
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
        switch (view.getId()) {
            case R.id.submit:
                Utils.checkDate(this);
                BdspRow.getInstance().send(getApplicationContext());
                ContentValues cv = BdspRow.getInstance().getRow();
                try {
                    db.insert(cv);
                } catch (SQLiteException e) {
                    Log.d("Database", "ERROR inserting: " + e.toString());
                }
                if (NetUpdateReceiver.netConnected) {
                    try {
                        AsyncTask<Void, Void, ArrayList<JSONArray>> doUpload = new UploadTask(Config.SUBMIT_URL);
                        doUpload.execute();
                    } catch (Exception e) {
                        Log.d("UPLOADER", "THAT DIDN'T WORK");
                    }
                }
                BdspRow.getInstance().clear();
                clearTextFields();
                break;
            default:
                System.out.println(view.getId());
                break;
        }
    }

    /**
     * Clears all editable text fields within a view object
     */
    private void clearTextFields() {
        /**
         * The viewgroup whose edittexts will be cleared
         */
        ViewGroup uniquesViewGroup = (ViewGroup) findViewById(R.id.uniques_view);
        for(int i = 0; i < uniquesViewGroup.getChildCount(); i++) {
            ViewGroup uniqueItemGroup = (ViewGroup) uniquesViewGroup.getChildAt(i);
            for(int j = 0; j < uniqueItemGroup.getChildCount(); j++) {
                if (uniqueItemGroup.getChildAt(j) instanceof EditText) {
                    ((EditText) uniqueItemGroup.getChildAt(j)).setText("");
                }
            }
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
        final EditText idTxt;
        idTxt = new EditText(this);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Login");
        adb.setMessage("Enter " + id_key);
        adb.setView(idTxt);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (idTxt.getText().toString().equals("")) {
                    showIdEntry(id_key);
                } else {
                    //config.setIdValue(idTxt.getText().toString().replace(' ', '_'));
                    String id = idTxt.getText().toString().replace(' ', '_');
                    BdspRow.setId(id);
                    getSupportActionBar().setSubtitle(config.getIdKey() + " : " + id );
                    config.updateUrl();
                }
            }
        });
        adb.setCancelable(false);
        adb.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

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
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
