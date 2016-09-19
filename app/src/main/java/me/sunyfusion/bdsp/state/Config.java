package me.sunyfusion.bdsp.state;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
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

    public boolean isGpsTrackerEnabled() {
        return gpsTrackerEnabled;
    }

    boolean gpsTrackerEnabled = false;
    private ID id;
    private Tracker tracker;
    private String project;
    ArrayList<Unique> uniques = new ArrayList<>();
    Context c;

    public Config(Context context) {
        c = context;   // Ties config to main activity
        init();
    }
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Type = readFile.getType();

            switch (Type) {
                case "url":
                    url = readFile.getArg(1);
                    SUBMIT_URL = readFile.getArg(1) + SUBMIT_URL;
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
                        latColumn = new Latitude(c, readFile.getArg(2));
                        lonColumn = new Longitude(c, readFile.getArg(3));
                    }
                    break;
                case "gpsTracker":
                    gpsTrackerEnabled = readFile.enabled();
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
        SUBMIT_URL += "?idk=" + id_key + "&idv=" + id_value + "&email=" + email + "&table=" + table;
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
        return true;
    }

    public void checkGPSPermission()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(c,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions((Activity) c,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Global.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.

        }
    }


}
