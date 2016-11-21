package me.sunyfusion.bdsp.state;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import cz.msebera.android.httpclient.Header;
import me.sunyfusion.bdsp.BdspRow;
import me.sunyfusion.bdsp.activity.MainActivity;
import me.sunyfusion.bdsp.db.BdspDB;
import me.sunyfusion.bdsp.exception.BdspConfigException;
import me.sunyfusion.bdsp.fields.Field;
import me.sunyfusion.bdsp.fields.FieldFactory;
import me.sunyfusion.bdsp.io.ReadFromInput;
import me.sunyfusion.bdsp.service.GpsService;

/**
 * Created by deisingj1 on 8/4/2016.
 */
public class BdspConfig {

    public ArrayList<Field> fields = new ArrayList<>();
    public static String SUBMIT_URL = "update.php";
    public String url;
    private String id_key = "";
    private BdspDB db;
    private String project;
    public String table = "";
    public boolean persistent_login = false;

    private Context c;

    public BdspConfig(Context context, InputStream stream) throws BdspConfigException{
        c = context;   // Ties config to main activity
        init(stream);
    }

    //TODO Currently not used, written to support updating configurations remotely, not finished
    public void getNewConfig() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://example.com/file.png", new FileAsyncHttpResponseHandler(c) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                // Do something with the file `response`
            }
            @Override
            public void onFailure(int statusCode, Header[] header, Throwable t, File f) {

            }
        });
    }

    private void addColumn(BdspRow.ColumnType type, String name) {
        if (db == null) {
            db = new BdspDB(c);
        }
        db.addColumn(name,"TEXT");
        BdspRow.ColumnNames.put(type,name);
    }

    private void init(InputStream file) throws BdspConfigException{
        String Type;

        String email = "";
        Scanner infile;
        try {
            infile = new Scanner(file);   // scans File
        } catch (Exception e) {
            throw new BdspConfigException();
        }
        ReadFromInput readFile = new ReadFromInput(infile);
        Field f;
        do {
            try {
                readFile.getNextLine();
                readFile.ReadLineCollectInfo();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Type = readFile.getType();

            switch (Type) {
                case "url":
                    if(url == null) {
                        url = readFile.getArg(1);
                        if(!SUBMIT_URL.contains(readFile.getArg(1))) {
                            SUBMIT_URL = readFile.getArg(1) + SUBMIT_URL;
                        }
                    }
                    break;
                case "project":
                    project = readFile.getArg(1);
                    break;
                case "locOnSub":
                    checkGPSPermission();
                    addColumn(BdspRow.ColumnType.LATITUDE, readFile.getArg(2));
                    addColumn(BdspRow.ColumnType.LONGITUDE, readFile.getArg(3));
                    break;
                case "email":
                    email = readFile.getArg(1);
                    break;
                case "id":
                    addColumn(BdspRow.ColumnType.ID,readFile.getArg(1));
                    id_key = readFile.getArg(1);
                    persistent_login = !readFile.getArg(2).equals("");
                    break;
                case "gpsLoc":
                    if (readFile.enabled()) {
                        checkGPSPermission();
                        addColumn(BdspRow.ColumnType.LATITUDE,readFile.getArg(2));
                        addColumn(BdspRow.ColumnType.LONGITUDE,readFile.getArg(3));
                    }
                    break;
                case "gpsTracker":
                    if (readFile.enabled()) {
                        addColumn(BdspRow.ColumnType.GEOMETRY, readFile.getArg(2));
                        addColumn(BdspRow.ColumnType.START,readFile.getArg(4));
                        addColumn(BdspRow.ColumnType.END,readFile.getArg(5));
                        BdspRow.getInstance().markStart();
                        checkGPSPermission();
                    }
                    break;
                case "datetime":
                    addColumn(BdspRow.ColumnType.DATE,readFile.getArg(1));
                    BdspRow.ColumnNames.put(BdspRow.ColumnType.DATE,readFile.getArg(1));
                    break;
                case "table":
                    table = readFile.getArg(1);
                    break;
                case "run":
                    addColumn(BdspRow.ColumnType.RUN,readFile.getArg(2));
                    BdspRow.ColumnNames.put(BdspRow.ColumnType.RUN,readFile.getArg(2));
                    break;
                case "photo":
                case "textfield":
                case "dropdown":
                case "bluetooth":
                    f = FieldFactory.build(c,readFile.getCurrentLine());
                    fields.add(f);
                default:
                    break;
            }
        }
        while (!Type.equals("endFile"));
        if(!email.isEmpty() && !table.isEmpty()) { updateUrl(email,table); }
        else throw new BdspConfigException();
    }

    private void updateUrl(String email, String table) {
        if(!SUBMIT_URL.contains("?email=") && !SUBMIT_URL.contains("&table=")) {
            SUBMIT_URL += "?email=" + email + "&table=" + table;
        }
    }

    public String getProjectUrl() {
        return url + "projects/" + project;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }
    public String getIdKey() {
        return id_key;
    }

    private void checkGPSPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(c,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions((Activity) c,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MainActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            }
            else {
                if(!isServiceRunning(GpsService.class)) {
                    c.startService(new Intent(c, GpsService.class));
                }
            }
        }
        else {
            if(!isServiceRunning(GpsService.class)) {
                c.startService(new Intent(c, GpsService.class));
            }
        }
    }
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
