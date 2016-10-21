package me.sunyfusion.bdsp;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import me.sunyfusion.bdsp.state.Global;

/**
 * Created by jesse on 10/13/16.
 */

public class Utils {

    public static String getDateString(String format) {
        SimpleDateFormat d = new SimpleDateFormat(format);
        d.setTimeZone(TimeZone.getDefault());
        String cdt = d.format(new java.util.Date());
        return cdt;
    }
    public static void checkDate(Context c) {
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
    public static int increment(Context c) {
        SharedPreferences prefs = c.getSharedPreferences("BDSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEdit = prefs.edit();
        int run = prefs.getInt("run",0);
        prefEdit.putInt("run",run+1);
        prefEdit.commit();
        return run;
    }
}
