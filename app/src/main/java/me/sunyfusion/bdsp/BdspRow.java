package me.sunyfusion.bdsp;

import android.content.ContentValues;

/**
 * Created by jesse on 10/17/16.
 */

public class BdspRow {
    private ContentValues row;
    private static String idk;
    private static String idv;

    //TODO add checking for whether or not column exists in db
    private static String DbColumns;

    public boolean put(String s, String v) {
        if(s != null && !s.isEmpty() && v != null) {
            row.put(s, v);
            return true;
        }
        return false;
    }
    public boolean append(String s, String v) {
        if(row.containsKey(s)) {
            String currentString = row.getAsString("s");
            row.put("s", currentString + v);
            return true;
        }
        else {
            return false;
        }
    }
    public ContentValues getRow() {
        return row;
    }
    public boolean send() {
        row.put(idk,idv);
        return true;
    }
}
