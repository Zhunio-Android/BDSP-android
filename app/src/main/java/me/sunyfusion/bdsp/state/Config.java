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
import java.util.ArrayList;
import java.util.Scanner;

import cz.msebera.android.httpclient.Header;
import me.sunyfusion.bdsp.column.Datestamp;
import me.sunyfusion.bdsp.column.ID;
import me.sunyfusion.bdsp.column.Latitude;
import me.sunyfusion.bdsp.column.Longitude;
import me.sunyfusion.bdsp.column.Photo;
import me.sunyfusion.bdsp.column.Run;
import me.sunyfusion.bdsp.column.Tracker;
import me.sunyfusion.bdsp.column.Unique;
import me.sunyfusion.bdsp.io.ReadFromInput;
import me.sunyfusion.bdsp.service.GpsService;

/**
 * Created by deisingj1 on 8/4/2016.
 */
public class Config {

    public static String SUBMIT_URL = "update.php";

    Latitude latColumn;
    Longitude lonColumn;
    String url;
    private String id_key, id_value, email, table;
    private Run run;
    private Photo photo;
    private Datestamp date;
    private boolean LocInUse;

    public boolean isGpsTrackerEnabled() {
        return gpsTrackerEnabled;
    }

    boolean gpsTrackerEnabled = false;
    private ID id;
    private String project;
    ArrayList<Unique> uniques = new ArrayList<>();
    Context c;

    public Config(Context context) {
        c = context;   // Ties config to main activity
        init();
    }
    //Currently not used, written to support updating configurations remotely, not finished
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

    private void init() {
        String Type;
        Scanner infile = null;
        try {
            infile = new Scanner(c.getAssets().open("buildApp.txt"));   // scans File
        } catch (Exception e) {
            e.printStackTrace();
        }
        ReadFromInput readFile = new ReadFromInput(infile);

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
                    latColumn = new Latitude(c, readFile.getArg(2));
                    lonColumn = new Longitude(c, readFile.getArg(3));
                    break;
                case "email":
                    email = readFile.getArg(1);
                    break;
                case "id":
                    id_key = readFile.getArg(1);
                    id = new ID(c, id_key);
                    break;
                case "photo":
                    if (readFile.enabled()) {
                        photo = new Photo(c,readFile.getArg(2));
                    }
                    break;
                case "gpsLoc":
                    if (readFile.enabled()) {
                        checkGPSPermission();
                        LocInUse = true;
                        latColumn = new Latitude(c, readFile.getArg(2));
                        lonColumn = new Longitude(c, readFile.getArg(3));
                    }
                    break;
                case "gpsTracker":
                    gpsTrackerEnabled = readFile.enabled();
                    if (readFile.enabled())
                        checkGPSPermission();
                    break;
                case "unique":
                    uniques.add(new Unique(c, readFile.getArg(1)));
                    break;
                case "datetime":
                    date = new Datestamp(c,readFile.getArg(1));
                    break;
                case "table":
                    table = readFile.getArg(1);
                    break;
                case "run":
                    run = new Run(c, readFile.getArg(2));
                    break;
                default:
                    break;
            }
        }
        while (!Type.equals("endFile"));

    }

    public void updateUrl() {
        if(!SUBMIT_URL.contains("?idk=") && !SUBMIT_URL.contains("&email=")) {
            SUBMIT_URL += "?idk=" + id_key + "&idv=" + id_value + "&email=" + email + "&table=" + table;
        }
        System.out.println(SUBMIT_URL);
    }

    public void setIdValue(String idValue) {
        id.setValue(idValue);
        id_value = idValue;
    }
    public String getProjectUrl() {
        return url + "projects/" + project;
    }

    public Run getRun() {
        return run;
    }
    public ArrayList<Unique> getUniques() {
        return uniques;
    }
    public String getIdKey() {
        return id_key;
    }
    public String getIdValue() {
        return id_value;
    }
    public Latitude getLatitude() { return latColumn; };
    public Longitude getLongitude() { return lonColumn; };
    public ID getId() { return id; }
    public boolean isPhotoEnabled() {
        return photo != null;
    }
    public boolean isLocationEnabled() {
        return LocInUse;
    }

    public void checkGPSPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(c,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions((Activity) c,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Global.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            }
        }
        else {
            if(!isServiceRunning(GpsService.class)) {
                Global.getContext().startService(new Intent(c, GpsService.class));
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
