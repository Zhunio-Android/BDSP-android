package me.sunyfusion.fuzion;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jesse on 5/5/16.
 */
public class Run {

    public static void checkDate() {
        SharedPreferences prefs = Global.getSharedPrefs();
        SharedPreferences.Editor prefEdit = Global.getInstance().prefEditor;
        String date = DateObject.getDate();
        if(prefs.getString("lastDate","").isEmpty()) {
            prefEdit.putString("lastDate",date);
            prefEdit.putInt("run",1);
            prefEdit.commit();
        }
        if (!prefs.getString("lastDate", "").equals(DateObject.getDate())) {
            prefEdit.putString("lastDate",date);
            prefEdit.putInt("run",1);
            prefEdit.commit();
        }
    }

    public static void insert(ContentValues values) {
        SharedPreferences prefs = Global.getSharedPrefs();
        int run = prefs.getInt("run",0);
        values.put("run", Integer.toString(run));
    }

    public static void increment() {
        SharedPreferences prefs = Global.getSharedPrefs();
        SharedPreferences.Editor prefEdit = Global.getInstance().prefEditor;
        int run = prefs.getInt("run",0);
        prefEdit.putInt("run",run+1);
        prefEdit.commit();
    }
}
