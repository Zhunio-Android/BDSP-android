package me.sunyfusion.fuzion;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.HashMap;

import me.sunyfusion.fuzion.column.Datestamp;
import me.sunyfusion.fuzion.db.BdspDB;
import me.sunyfusion.fuzion.hardware.GPS;

/**
 * Created by jesse on 8/1/16.
 */
public class Global {
    private Context context;
    private static Global ourInstance = new Global();

    boolean trackingActive = false;

    Uri imgUri;
    BdspDB dbHelper;
    SQLiteDatabase db;
    public GPS gps;
    Datestamp datestamp;
    ContentValues values;
    HashMap<String, Boolean> enabledFeatures = new HashMap<String, Boolean>();
    HashMap<String, String> config = new HashMap<String, String>();
    SharedPreferences sharedPref;
    SharedPreferences.Editor prefEditor;
    private static String SUBMIT_URL = "http://www.sunyfusion.me/ft_test/update.php";


    public static Global getInstance() {
        return ourInstance;
    }

    private Global() {

    }

    public void init(Context context) {
        this.context = context;
    }

    public static String getConfig(String s) {
        return getInstance().config.get(s);
    }

    public static void setConfig(String k, String v) {
        getInstance().config.put(k, v);
    }

    public static String getSubmitUrl() {
        String id_key = getConfig("id_key");
        String id_value = getConfig("id_value");
        String email = getConfig("email");
        String table = getConfig("table");

        return SUBMIT_URL + "?idk=" + id_key + "&idv=" + id_value + "&email=" + email + "&table=" + table;
    }

    public static Uri getimgUri() {
        return getInstance().imgUri;
    }

    public static SQLiteDatabase getDb() {
        return getInstance().db;
    }

    public static BdspDB getDbHelper() {
        return getInstance().dbHelper;
    }

    public static void setEnabled(String s) {
        getInstance().enabledFeatures.put(s, true);
    }

    public static boolean isEnabled(String s) {
        if (getInstance().enabledFeatures.containsKey(s)) {
            return getInstance().enabledFeatures.get(s);
        } else return false;
    }

    public static SharedPreferences getSharedPrefs() {
        return getInstance().sharedPref;
    }

    public static ContentValues getValues() {
        return getInstance().values;
    }

    public static void clearValues() {
        getInstance().values = new ContentValues();
    }

    public static Context getContext() {
        return getInstance().context;
    }

    public static boolean isTrackingActive() {
        return getInstance().trackingActive;
    }

    public static void setTracking(boolean b) {
        getInstance().trackingActive = b;
    }
}
