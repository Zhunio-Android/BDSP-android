package me.sunyfusion.bdsp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import me.sunyfusion.bdsp.activity.MainActivity;
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
            prefEdit.putString("lastDate",date);
            prefEdit.putInt("run",1);
            prefEdit.commit();
        }
    }
    public static int increment(Context c) {
        SharedPreferences prefs = c.getSharedPreferences("BDSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEdit = prefs.edit();
        int run = prefs.getInt("run", 1);
        prefEdit.putInt("run", run + 1);
        prefEdit.commit();
        return run;
    }
    public static File[] getPhotoList(Context c) {
        File path = c.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] files = path.listFiles();
        for(File f : files) {
            System.out.println(f.getName());
        }
        return files;
    }
    public static boolean deletePhoto(Context c, File f) {
        try {
            return f.getCanonicalFile().delete();
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public static String[] stringToArray(String s) {
        return s.substring(1, s.length()-1).split(",");
    }
    public static void test() {
        View v = ((MainActivity) Global.getContext()).getView("Thing");
        if(v != null) ((EditText) v).setText("TEST !");
    }
}
