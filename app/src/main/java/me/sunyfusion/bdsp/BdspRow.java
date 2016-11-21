package me.sunyfusion.bdsp;

import android.content.ContentValues;
import android.content.Context;
import android.location.Location;

import java.util.HashMap;

/**
 * Created by jesse on 10/17/16.
 */

public class BdspRow {
    private ContentValues row;
    public enum ColumnType {
        DATE, ID, LATITUDE, LONGITUDE, PHOTO, RUN, GEOMETRY, START, END, UNIQUE
    }

    final String kmlStart = "<LineString><tessellate>1</tessellate><coordinates>";
    final String kmlEnd = "</coordinates></LineString>";

    private static String idv = "";
    public static HashMap<ColumnType,String> ColumnNames = new HashMap<>();
    private static BdspRow ourInstance = new BdspRow();

    //TODO add checking for whether or not column exists in db

    public BdspRow() {
        row = new ContentValues();
    }

    public static BdspRow getInstance() {
        return ourInstance;
    }

    public boolean put(String s, String v) {
        System.out.println("key:" + s + " value:" + v);
        if(s != null && !s.isEmpty() && v != null) {
            print();
            row.put(s, v);
            return true;
        }
        return false;
    }

    /**
     * Appends value without replacing it
     * @param s key
     * @param v value
     * @return True if a value was appended, false if a new value was created (no previous value to append)
     */
    public boolean append(String s, String v) {
        if(row.containsKey(s)) {
            String currentString = row.getAsString(s);
            row.put(s, currentString + v);
            return true;
        }
        else {
            put(s,v);
            return false;
        }
    }
    public ContentValues getRow() {
        return row;
    }
    public boolean send(Context c) {
        addSpecialColumns(c);
        //clear();
        return true;
    }
    private void addSpecialColumns(Context c) {
        for(ColumnType column : ColumnNames.keySet()) {
            switch(column) {
                case ID:
                    put(ColumnNames.get(ColumnType.ID), idv);
                    break;
                case RUN:
                    put(ColumnNames.get(ColumnType.RUN), Integer.toString(Utils.increment(c)));
                    break;
                case GEOMETRY:
                    String s = row.getAsString(ColumnNames.get(ColumnType.GEOMETRY));
                    put(ColumnNames.get(ColumnType.GEOMETRY), kmlStart + s + kmlEnd);
                    System.out.println(row.get(ColumnNames.get(ColumnType.GEOMETRY)));
                    break;
                case DATE:
                case END:
                    put(ColumnNames.get(column), Utils.getDateString("yyyy-MM-dd HH:mm:ss"));
                    break;
                case LATITUDE:
                case LONGITUDE:
                    break;
                default: break;
            }
        }
    }
    public boolean attachLocation(Location l) {
        if(l != null && ColumnNames.containsKey(ColumnType.LATITUDE) && ColumnNames.containsKey(ColumnType.LONGITUDE)) {
            row.put(ColumnNames.get(ColumnType.LATITUDE), l.getLatitude());
            row.put(ColumnNames.get(ColumnType.LONGITUDE), l.getLongitude());
            return true;
        }
        else {
            return false;
        }
    }
    public void addToKml(Location l) {
        if(l != null && !idv.equals("") &&ColumnNames.containsKey(ColumnType.GEOMETRY)) {
            BdspRow.getInstance().append(BdspRow.ColumnNames.get(BdspRow.ColumnType.GEOMETRY), l.getLongitude() + "," + l.getLatitude() + " ");
        }
    }
    public static void setId(String s) {
        idv = s;
    }
    public static String getId() {
        return idv;
    }
    public static void clearId() {
        idv = "";
    }
    public void print() {
        System.out.println(row.toString());
    }
    public void clear() {
        row = new ContentValues();
        BdspRow.getInstance().markStart();
    }
    public static boolean hasColumn(ColumnType col) {
        return ColumnNames.containsKey(col);
    }
    public boolean markStart() {
        if(ColumnNames.containsKey(ColumnType.START)) {
            put(ColumnNames.get(ColumnType.START), Utils.getDateString("yyyy-MM-dd HH:mm:ss"));
            return true;
        }
        return false;
    }
}
