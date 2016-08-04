package me.sunyfusion.fuzion;

import android.content.Context;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import me.sunyfusion.fuzion.column.Camera;
import me.sunyfusion.fuzion.column.Datestamp;
import me.sunyfusion.fuzion.column.Run;
import me.sunyfusion.fuzion.column.Unique;
import me.sunyfusion.fuzion.db.BdspDB;

/**
 * Created by deisingj1 on 8/4/2016.
 */
public class Config {

    public static String SUBMIT_URL = "http://www.sunyfusion.me/ft_test/update.php";

    private String dateColumn, cameraColumn, latColumn, lonColumn, trackerLatColumn, trackerLonColumn, runColumn;
    private String id_key, id_value, email, table;
    private Run run;
    private Camera camera;
    private Datestamp date;
    ArrayList<Unique> uniques = new ArrayList<>();
    Context c;

    public Config(Context context) {
        c = context;
        init();
    }
    public Run getRun() {
        return run;
    }
    public ArrayList<Unique> getUniques() {
        return uniques;
    }
    public String getDateColumn() {
        return dateColumn;
    }
    public String getIdKey() {
        return id_key;
    }
    private void init() {
        String Type, Name;
        Scanner infile = null;
        BdspDB dbHelper = new BdspDB(c);

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
                case "locOnSub":
                    Global.setEnabled("addLocationToSubmission");
                    dbHelper.addColumn("latitude", "TEXT");
                    dbHelper.addColumn("longitude", "TEXT");
                    break;
                case "email":
                    email = readFile.getArg(1);
                    break;
                case "id":
                    id_key = readFile.getArg(1);
                    break;
                case "camera":
                    if (readFile.enabled()) {
                        camera = new Camera(c,readFile.getArg(2));
                        Global.getDbHelper().addColumn(cameraColumn, "TEXT");
                    }
                    break;
                case "gpsLoc":
                    if (readFile.enabled()) {
                        latColumn = readFile.getArg(2);
                        lonColumn = readFile.getArg(3);
                        Global.getDbHelper().addColumn(latColumn, "TEXT");
                        Global.getDbHelper().addColumn(lonColumn, "TEXT");
                        //new GPS(c, latColumn, lonColumn);
                    }
                    break;
                case "gpsTracker":/*
                    if (readFile.enabled()) {
                        Global.setEnabled("gpsTracking");
                        if (Global.getInstance().gps == null) {
                            new GPS(Global.getContext(), "latitude", "longitude");
                        }
                        GPS gps = Global.getInstance().gps;
                        if (args.length > 1 && args[2] != null) {
                            gps.setGpsFreq(Integer.parseInt(args[2]));
                            try {

                            } catch (SecurityException e) {

                            }
                        }
                        if (args.length > 3) {
                            dbHelper.addColumn(args[3], "TEXT");
                            gps.gps_tracker_lat = args[3];
                            gps.gps_tracker_long = args[4];
                            dbHelper.addColumn(args[4], "TEXT");
                        }
                        Global.setTracking(true);
                    }*/
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
        SUBMIT_URL += "?idk=" + id_key + "&idv=" + Global.getConfig("id_value") + "&email=" + email + "&table=" + table;
    }
    public void setIdValue(String idValue) {
        id_value = idValue;
    }
    public String getIdValue() {
        return id_value;
    }
}
