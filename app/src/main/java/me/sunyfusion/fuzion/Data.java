package me.sunyfusion.fuzion;

import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import me.sunyfusion.fuzion.column.Datestamp;
import me.sunyfusion.fuzion.db.BdspDB;

/**
 * Created by jesse on 8/1/16.
 */
public class Data {

    public static void submit(Config config) {
        ContentValues values = new ContentValues();
        double latitude = 0; //GPS.latitude;
        double longitude = 0; //GPS.longitude;
/*
        Run.checkDate();          // Compares dates for persistent variables
        Run.insert(values);   // Inserts persistent into ContentValue object
        Run.increment();                          // Increments persistent variable
*/
        values.put(config.getIdKey(), config.getIdValue());
        if (Global.isEnabled("addLocationToSubmission")) {
            values.put("longitude", longitude);
            values.put("latitude", latitude);
        }
        BdspDB.enterUniquesToDatabase(config.getUniques());
        values.put(config.getDateColumn(), Datestamp.getDateString());
        try {
            Global.getDb().insert("tasksTable", null, values);
        } catch (SQLiteException e) {
            Log.d("Database", "ERROR inserting: " + e.toString());
        }

    }

}

