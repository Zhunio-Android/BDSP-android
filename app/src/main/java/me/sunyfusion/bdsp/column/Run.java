package me.sunyfusion.bdsp.column;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import me.sunyfusion.bdsp.activity.MainActivity;
import me.sunyfusion.bdsp.service.GpsService;
import me.sunyfusion.bdsp.state.Global;

/**
 * Created by jesse on 5/5/16.
 */
public class Run extends Column{
    Context c;
    public Run(Context context, String name) {
        super(context,name);
        c = context;
    }

    /**
     * checks to see if the current date is different than the one set in SharedPreferences,
     * if it is (signifying the start of a new day), resets the run number (also in SharedPreferences)
     * to 1, and the date stored in SharedPreferences to "now".
     */
    public void checkDate() {
        SharedPreferences prefs = c.getSharedPreferences("BDSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEdit = prefs.edit();
        String date = Datestamp.getDateString("yyyy-MM-dd");
        Log.d("BDSPDATE", date);
        if(prefs.getString("lastDate","").isEmpty()) {
            prefEdit.putString("lastDate",date);
            prefEdit.putInt("run",1);
            prefEdit.commit();
        }
        if (!prefs.getString("lastDate", "").equals(Datestamp.getDateString("yyyy-MM-dd"))) {
            if(Datestamp.getDateString("HH:mm:ss").contains("00:00:0")) {
                c.stopService(new Intent(c,GpsService.class));
                ((Activity) c).finishAffinity();
            }
            else {
                Global.getDb().deleteRun(prefs.getInt("run", 1), prefs.getString("lastDate", ""));
                prefEdit.putString("lastDate", date);
                prefEdit.putInt("run", 1);
                prefEdit.commit();
            }
        }
    }
    @Override
    public void insertValue(ContentValues values) {
        SharedPreferences prefs = c.getSharedPreferences("BDSP", Context.MODE_PRIVATE);
        int run = prefs.getInt("run",0);
        values.put("run", Integer.toString(run));
        increment();
    }

    /**
     * Inserts the current run number into a ContentValues object
     * @param values the ContentValues object to insert the run number into
     */
    public void insertValueNoInc(ContentValues values) {
        SharedPreferences prefs = c.getSharedPreferences("BDSP", Context.MODE_PRIVATE);
        int run = prefs.getInt("run",0);
        values.put("run", Integer.toString(run));
    }

    /**
     * Increments the currently stored run number in SharedPreferences
     */
    public void increment() {
        SharedPreferences prefs = c.getSharedPreferences("BDSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEdit = prefs.edit();
        int run = prefs.getInt("run",0);
        prefEdit.putInt("run",run+1);
        prefEdit.commit();
    }
}
