//TODO have datatypes option for unique fields - shelved
//TODO autoincrement field
//TODO section function - auto increment by date on field
//TODO flagged fields - allow binary
//TODO manual or incremental field - trigger = field, reset = how you want to reset (daily or no reset)
//TODO housekeeping, separate manual and automatically collected

//TODO add fields for GPS feedback

package me.sunyfusion.fuzion.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;

import me.sunyfusion.fuzion.R;
import me.sunyfusion.fuzion.adapter.UniqueAdapter;
import me.sunyfusion.fuzion.column.Unique;
import me.sunyfusion.fuzion.db.BdspDB;
import me.sunyfusion.fuzion.receiver.NetUpdateReceiver;
import me.sunyfusion.fuzion.service.trackerService;
import me.sunyfusion.fuzion.state.Config;
import me.sunyfusion.fuzion.state.Global;
import me.sunyfusion.fuzion.tasks.UploadTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // CONSTANTS
    MenuItem cameraMenu;
    MenuItem gpsMenu;
    Context context = this;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    trackerService trackerServiceBinder;
    Config config;
    BdspDB db;
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
        config = new Config(this);
        db = Global.getDb();
        Global.getInstance().setConfig(config);
        ArrayList<Unique> uniques = config.getUniques();
        showIdEntry(config.getIdKey());
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setSubtitle(config.getIdKey() + " : " + config.getIdValue());
        mRecyclerView = (RecyclerView) findViewById(R.id.uniques_view);
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

        startService(new Intent(MainActivity.this, trackerService.class));
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
        NetUpdateReceiver.netConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
        Log.i("NET", "Network Connected: " + NetUpdateReceiver.netConnected);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this,trackerService.class));
        Global.getConfig().getGps().stopLocationUpdates();
        db.close();
        super.onDestroy();
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
        switch (view.getId()) {
            case R.id.submit:
                Intent intent = new Intent("save-all-columns");
                ContentValues cv = new ContentValues();
                intent.putExtra("cv", cv);
                LocalBroadcastManager.getInstance(this).sendBroadcastSync(intent);
                try {
                    db.insert(cv);
                } catch (SQLiteException e) {
                    Log.d("Database", "ERROR inserting: " + e.toString());
                }
                if (NetUpdateReceiver.netConnected) {
                    try {
                        AsyncTask<Void, Void, JSONArray> doUpload = new UploadTask();
                        doUpload.execute();
                    } catch (Exception e) {
                        Log.d("UPLOADER", "THAT DIDN'T WORK");
                    }
                }
                clearTextFields();
                break;
            default:
                break;
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (trackerService.class.getName().equals(
                    service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public ServiceConnection bdspConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            trackerServiceBinder = ((trackerService.bdspBinder) iBinder).getService();
            Log.d("ServiceConnection", "connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            trackerServiceBinder = null;
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
        intent = new Intent(this, trackerService.class);
        Messenger messenger = new Messenger(myHandler);
        intent.putExtra("MESSENGER", messenger);
        bindService(intent, bdspConnection, Context.BIND_AUTO_CREATE);
    }

    private void clearTextFields() {
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
                    config.setIdValue(idTxt.getText().toString().replace(' ', '_'));
                    getSupportActionBar().setSubtitle(config.getIdKey() + " : " + config.getIdValue());
                    config.updateUrl();
                }
            }
        });
        adb.setCancelable(false);
        adb.show();
    }
}
