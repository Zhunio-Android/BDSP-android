package me.sunyfusion.bdsp.column;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import me.sunyfusion.bdsp.db.BdspDB;

/**
 * @author Jesse Deisinger
 * @version 8.4.16
 */
public class Column {
    public enum ColumnType {
        DATE, ID, LATITUDE, LONGITUDE, UNIQUE, PHOTO
    }
    /**
     * Name of column in database and fusion table
     */
    private String columnName;
    /**
     * Holds present value of the column
     */
    private String value = "";
    private BdspDB db;
    final Column column = this;
    ColumnType columnType;


    /**
     * Default constructor
     * @param colName name of column read in from configuration file
     */
    public Column(LocalBroadcastManager lbm, ColumnType type, String colName, BdspDB db) {
        columnName = colName;
        columnType = type;
        db.addColumn(colName, "TEXT");
        lbm.registerReceiver(receiver, new IntentFilter("save-all-columns"));
    }

    /**
     * Receives broadcast from UI that the Submit button was pressed, and inserts
     * the value of the column into the ContentValues object passed to it in the
     * intent.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ContentValues cv = (ContentValues) intent.getExtras().get("cv");
                insertValue(cv);
                if(!column.isType(ColumnType.ID)) {
                    value = "";
                }
        }
    };

    /**
     * Inserts the name and value of the column into a contentvalues object
     * @param v The object to insert the column's value into
     */
    public void insertValue(ContentValues v) {
        switch(columnType) {
            case UNIQUE:
            case ID:
            case PHOTO:
            case LATITUDE:
            case LONGITUDE:
                if(columnName != null && value != null) {
                    v.put(columnName, value);
                }
                break;
            default: break;
        }
    }

    /**
     * Gets column name
     * @return Column name
     */
    public String getName() {
        return columnName;
    }

    /**
     * Sets column value
     * @param v New column value
     */
    public void setValue(String v) {
        value = v;
    }

    /**
     * Gets column value
     * @return Column value
     */
    public String getValue() { return value; }
    public boolean isType(ColumnType type) {
        return columnType == type;
    }
}
