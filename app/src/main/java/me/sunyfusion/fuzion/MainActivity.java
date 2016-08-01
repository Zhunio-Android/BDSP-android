//TODO have datatypes option for unique fields - shelved
//TODO autoincrement field
//TODO section function - auto increment by date on field
//TODO flagged fields - allow binary
//TODO manual or incremental field - trigger = field, reset = how you want to reset (daily or no reset)
//TODO housekeeping, separate manual and automatically collected

//TODO add fields for GPS feedback

package me.sunyfusion.fuzion;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONArray;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // CONSTANTS

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * Runs on startup, creates the layout when the activity is created.
     * This is essentially the "main" method.
     *
     * @param savedInstanceState restores previous state on entry
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.getInstance().init(this);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        ArrayList<Unique> uniques = dispatch();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(uniques.size());

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new UniqueAdapter(uniques);
        mRecyclerView.setAdapter(mAdapter);

        //Setup environment
        Run.checkDate();
    }

    @Override
    public void onStart() {
        super.onStart();
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        UpdateReceiver.netConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
        Log.i("NET", "Network Connected: " + UpdateReceiver.netConnected);
        Run.checkDate();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Global.getInstance().db.close();
        try {
            //UiBuilder.wakelock.release();
        } catch (Exception e) {
        }
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
            Global.getInstance().imgUri = data.getData();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                Data.submit();
                break;
            default:
                break;
        }
    }

    public void showIdEntry(final String[] args) {
        final EditText idTxt;
        DatabaseHelper dbHelper = Global.getInstance().dbHelper;
        idTxt = new EditText(this);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Login");
        adb.setMessage("Enter " + args[1]);
        adb.setView(idTxt);
        dbHelper.addColumn(args[1], "TEXT");
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (idTxt.getText().toString().equals("")) {
                    showIdEntry(args);
                } else {
                    getSupportActionBar().setSubtitle(args[1] + " : " + idTxt.getText().toString().replace(' ', '_'));
                    Global.setConfig("id_key", args[1]);
                    Global.setConfig("id_value", idTxt.getText().toString().replace(' ', '_'));
                }
            }
        });
        adb.setCancelable(false);
        adb.show();
    }

    public ArrayList<Unique> dispatch() {
        ArrayList<Unique> uniques = new ArrayList<>();
        String Type, Name;
        Scanner infile = null;
        DatabaseHelper dbHelper = Global.getDbHelper();

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
                case "locOnSub":
                    Global.setEnabled("addLocationToSubmission");
                    dbHelper.addColumn("latitude", "TEXT");
                    dbHelper.addColumn("longitude", "TEXT");
                    break;
                case "email":
                    Global.setConfig("email", readFile.getArg(1));
                    break;
                case "id":
                    showIdEntry(readFile.getArgs());
                    break;
                case "camera":
                    if (readFile.enabled()) {
                        Global.getDbHelper().addColumn(readFile.getArg(2), "TEXT");
                        Global.setEnabled("camera");
                    }
                    break;
                case "gpsLoc":
                    if (readFile.enabled()) {
                        Global.setEnabled("gpsLocation");
                        final String latColumn = readFile.getArg(2);
                        final String longColumn = readFile.getArg(3);
                        new GPSHelper(this, latColumn, longColumn);
                    }
                    break;
                case "gpsTracker":
                    if (readFile.enabled()) {
                        UiBuilder.gpsTracker(readFile.getArgs(), dbHelper, this);
                        if (!isMyServiceRunning()) {
                            startService(new Intent(this, GPSService.class));
                        }
                    }
                    break;
                case "unique":
                    Name = readFile.getUniqueName();
                    Unique u = new Unique(this, readFile.getArgs());
                    uniques.add(u);
                    dbHelper.addColumn(Name, "TEXT");
                    break;
                case "datetime":
                    Global.getInstance().date = new DateObject(readFile.getArgs()[1]);
                    dbHelper.addColumn(readFile.getArgs()[1], "TEXT");
                    break;
                case "table":
                    Global.setConfig("table", readFile.getArgs()[1]);
                    break;
                case "run":
                    dbHelper.addColumn(readFile.getArgs()[2], "TEXT");
                    Global.setEnabled("includeRunInSubmission");
                    break;
                default:
                    break;
            }
        }
        while (!Type.equals("endFile"));
        return uniques;
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (GPSService.class.getName().equals(
                    service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
