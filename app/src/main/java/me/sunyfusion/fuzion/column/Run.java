package me.sunyfusion.fuzion.column;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jesse on 5/5/16.
 */
public class Run extends Column{
    Context c;
    public Run(Context context, String name) {
        super(context,name);
        c = context;
    }

    public void checkDate() {
        SharedPreferences prefs = c.getSharedPreferences("BDSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEdit = prefs.edit();
        String date = Datestamp.getDateString();
        if(prefs.getString("lastDate","").isEmpty()) {
            prefEdit.putString("lastDate",date);
            prefEdit.putInt("run",1);
            prefEdit.commit();
        }
        if (!prefs.getString("lastDate", "").equals(Datestamp.getDateString())) {
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
    public void insertValueNoInc(ContentValues values) {
        SharedPreferences prefs = c.getSharedPreferences("BDSP", Context.MODE_PRIVATE);
        int run = prefs.getInt("run",0);
        values.put("run", Integer.toString(run));
    }

    public void increment() {
        SharedPreferences prefs = c.getSharedPreferences("BDSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEdit = prefs.edit();
        int run = prefs.getInt("run",0);
        prefEdit.putInt("run",run+1);
        prefEdit.commit();
    }
}
