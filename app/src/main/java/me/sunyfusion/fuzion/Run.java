package me.sunyfusion.fuzion;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jesse on 5/5/16.
 */
public class Run {

    public static void checkDate(Context c, SharedPreferences prefs) {
        SharedPreferences.Editor prefEdit = prefs.edit();
        String date = DateHelper.getDate();
        if(prefs.getString("lastDate","").isEmpty()) {
            prefEdit.putString("lastDate",date);
            prefEdit.putInt("run",1);
            prefEdit.commit();
        }
        if(!prefs.getString("lastDate","").equals(DateHelper.getDate())) {
            prefEdit.putString("lastDate",date);
            prefEdit.putInt("run",1);
            prefEdit.commit();
        }
    }
    public static void insert(Context c, SharedPreferences prefs, ContentValues values) {
        SharedPreferences.Editor prefEdit = prefs.edit();
        int run = prefs.getInt("run",0);
        System.out.println("RUN = " + run);
        values.put("run", run);
    }
    public static void increment(SharedPreferences prefs) {
        int run = prefs.getInt("run",0);
        SharedPreferences.Editor prefEdit = prefs.edit();
        prefEdit.putInt("run",run+1);
        prefEdit.commit();
    }
}
