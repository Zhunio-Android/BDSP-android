package me.sunyfusion.bdsp.column;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import me.sunyfusion.bdsp.db.BdspDB;
import me.sunyfusion.bdsp.state.Global;

/**
 * @author Jesse Deisinger
 * @version 8.4.16
 */
public abstract class Column {
    /**
     * Name of column in database and fusion table
     */
    private String columnName;
    /**
     * Holds present value of the column
     */
    private String value = "";
    private Context c;
    private BdspDB db;
    final Column column = this;

    /**
     * Default constructor
     * @param context context passed in from calling method
     * @param colName name of column read in from configuration file
     */
    public Column(Context context, String colName) {

        c = context;
        columnName = colName;
        db = Global.getDb();
        db.addColumn(colName, "TEXT");
        LocalBroadcastManager.getInstance(c).registerReceiver(receiver, new IntentFilter("save-all-columns"));
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
                if(!(column instanceof ID)) {
                    value = "";
                }
        }
    };

    /**
     * Inserts the name and value of the column into a contentvalues object
     * @param v The object to insert the column's value into
     */
    public void insertValue(ContentValues v) {
        v.put(columnName,value);
    }

    /**
     * Gets column name
     * @return Column name
     */
    public String getColumnName() {
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
    public Context getContext() { return c; }

}
