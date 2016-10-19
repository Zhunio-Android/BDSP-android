package me.sunyfusion.bdsp.column;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import me.sunyfusion.bdsp.Utils;
import me.sunyfusion.bdsp.db.BdspDB;
import me.sunyfusion.bdsp.state.Global;

/**
 * Created by jesse on 5/5/16.
 */
public class Run extends Column{
    Context c;
    public Run(Context context, String name, BdspDB db) {

        super(LocalBroadcastManager.getInstance(context),ColumnType.UNIQUE,name,db);
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
        String date = Utils.getDateString("yyyy-MM-dd");
        if(prefs.getString("lastDate","").isEmpty()) {
            prefEdit.putString("lastDate",date);
            prefEdit.putInt("run",1);
            prefEdit.commit();
        }
        if (!prefs.getString("lastDate", "").equals(Utils.getDateString("yyyy-MM-dd"))) {
            Global.getDb().deleteRun(prefs.getInt("run",1),prefs.getString("lastDate", ""));
            prefEdit.putString("lastDate",date);
            prefEdit.putInt("run",1);
            prefEdit.commit();
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
