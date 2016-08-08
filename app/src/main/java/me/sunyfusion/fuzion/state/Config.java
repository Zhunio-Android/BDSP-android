package me.sunyfusion.fuzion.state;

import android.content.Context;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import me.sunyfusion.fuzion.column.Datestamp;
import me.sunyfusion.fuzion.column.ID;
import me.sunyfusion.fuzion.column.Latitude;
import me.sunyfusion.fuzion.column.Longitude;
import me.sunyfusion.fuzion.column.Photo;
import me.sunyfusion.fuzion.column.Run;
import me.sunyfusion.fuzion.column.Tracker;
import me.sunyfusion.fuzion.column.Unique;
import me.sunyfusion.fuzion.hardware.GPS;
import me.sunyfusion.fuzion.io.ReadFromInput;

/**
 * Created by deisingj1 on 8/4/2016.
 */
public class Config {

    public static String SUBMIT_URL = "http://www.sunyfusion.me/ft_test/update.php";

    Latitude latColumn;
    Longitude lonColumn;
    private String id_key, id_value, email, table;
    private Run run;
    private Photo photo;
    private Datestamp date;
    private ID id;
    private Tracker tracker;
    ArrayList<Unique> uniques = new ArrayList<>();
    Context c;
    GPS gps;

    public Config(Context context) {
        c = context;
        init();
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
                case "locOnSub":
                    if(gps == null) {
                        gps = new GPS(c);
                    }
                    latColumn = new Latitude(c, readFile.getArg(2), gps);
                    lonColumn = new Longitude(c, readFile.getArg(3), gps);
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
                        if(gps == null) {
                            gps = new GPS(c);
                        }
                        latColumn = new Latitude(c, readFile.getArg(2), gps);
                        lonColumn = new Longitude(c, readFile.getArg(3), gps);
                    }
                    break;
                case "gpsTracker":
                    if(readFile.enabled()) {
                        if(gps == null) {
                            gps = new GPS(c);
                        }
                        tracker = new Tracker(c, this);
                    }
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
    public GPS getGps(){
        return gps;
    }
    public ID getId() { return id; }
}
