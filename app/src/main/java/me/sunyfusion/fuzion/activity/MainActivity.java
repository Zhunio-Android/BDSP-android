//TODO have datatypes option for unique fields - shelved
//TODO autoincrement field
//TODO section function - auto increment by date on field
//TODO flagged fields - allow binary
//TODO manual or incremental field - trigger = field, reset = how you want to reset (daily or no reset)
//TODO housekeeping, separate manual and automatically collected

//TODO add fields for GPS feedback

package me.sunyfusion.fuzion.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import me.sunyfusion.fuzion.bdspService;
import me.sunyfusion.fuzion.Data;
import me.sunyfusion.fuzion.db.DatabaseHelper;
import me.sunyfusion.fuzion.DateObject;
import me.sunyfusion.fuzion.GPSHelper;
import me.sunyfusion.fuzion.GPSService;
import me.sunyfusion.fuzion.Global;
import me.sunyfusion.fuzion.R;
import me.sunyfusion.fuzion.ReadFromInput;
import me.sunyfusion.fuzion.Run;
import me.sunyfusion.fuzion.UiBuilder;
import me.sunyfusion.fuzion.Unique;
import me.sunyfusion.fuzion.UniqueAdapter;
import me.sunyfusion.fuzion.UpdateReceiver;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // CONSTANTS
    MenuItem cameraMenu;
    MenuItem gpsMenu;
    Context context = this;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    bdspService bdspServiceBinder;


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
        ArrayList<Unique> uniques = dispatch();
        startService(new Intent(MainActivity.this, bdspService.class));
        doBindService();
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setSubtitle(Global.getConfig("id_key") + " : " + Global.getConfig("id_value"));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);

        gpsMenu = menu.getItem(1);
        if (Global.isEnabled("gpsLocation")) {
            gpsMenu.setVisible(true);
        }
        cameraMenu = menu.getItem(0);
        if (Global.isEnabled("camera")) {
            cameraMenu.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_mode_close_button) {
            stopService(new Intent(this, GPSService.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
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
        unbindService(bdspConnection);
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

    public ServiceConnection bdspConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bdspServiceBinder = ((bdspService.bdspBinder) iBinder).getService();
            Log.d("ServiceConnection", "connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bdspServiceBinder = null;
            Log.d("ServiceConnection", "disconnected");
        }
    };
    public Handler myHandler = new Handler() {
        public void handleMessage(Message message) {
            Bundle data = message.getData();
        }
    };

    public void doBindService() {
        Intent intent = null;
        intent = new Intent(this, bdspService.class);
        Messenger messenger = new Messenger(myHandler);
        intent.putExtra("MESSENGER", messenger);
        bindService(intent, bdspConnection, Context.BIND_AUTO_CREATE);
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
                        Global.setEnabled("gpsTracking");
                        UiBuilder.gpsTracker(readFile.getArgs(), dbHelper, this);
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

    public void showIdEntry(final String[] args) {
        final EditText idTxt;
        DatabaseHelper dbHelper = Global.getInstance().dbHelper;
        idTxt = new EditText(Global.getContext());
        AlertDialog.Builder adb = new AlertDialog.Builder(Global.getContext());
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
                    Global.setConfig("id_key", args[1]);
                    Global.setConfig("id_value", idTxt.getText().toString().replace(' ', '_'));
                }
            }
        });
        adb.setCancelable(false);
        adb.show();
    }
}
