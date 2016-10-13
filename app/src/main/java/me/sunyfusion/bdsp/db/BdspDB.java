package me.sunyfusion.bdsp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import me.sunyfusion.bdsp.state.Global;

/**
 * Created by Robert Wieland on 3/26/16.
 */

public class BdspDB extends SQLiteOpenHelper
{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Tasks.db";
    private static final String TABLE_NAME = "tasksTable";
    private SQLiteDatabase db;

    public BdspDB(Context c)
    {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + TABLE_NAME + " (unique_table_id INTEGER PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
    /**
     * Creates new BdspDB object
     * @param context the context of the Activity that is using this object
     */


    /**
     * Inserts values into database
     * @param values values to be inserted
     */
    public long insert(ContentValues values) {
        return db.insert(TABLE_NAME,null,values);
    }

    /**
     * Adds a column to the database
     * @param columnName name of the column
     * @param columnType type of the column
     */
    public void addColumn(String columnName, String columnType)
    {
        try {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + columnName + " " + columnType);
        }
        catch(SQLiteException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Queries the default table, returning a cursor to every row in the table
     * @return a cursor to all of the rows in the table
     */
    public Cursor queueAll(Object[] o) {
        Cursor cursor;
        if(o != null) {
            String[] s = new String[]{(String) o[0], (String) o[1] + "%"};
            cursor = db.query("tasksTable", null, "run=? and date LIKE ?",
                    s, null, null, null);
        }
        else {
            cursor = db.query("tasksTable", null, null,
                    null, null, null, null);
        }
        return cursor;
    }
    public HashSet<ArrayList<String>> getColumns(String... column_names) {
        if (db == null) throw new AssertionError("db is null, there's a problem");
        HashSet<ArrayList<String>> columnsList = new HashSet<ArrayList<String>>();
        Cursor c = db.query(true, "tasksTable", column_names, null, null, null, null, null, null);
        if (c.getCount() > 0) {
            Log.d("DB SIZE", Integer.toString(c.getCount()));
            c.moveToNext();
            while (!c.isAfterLast()) {
                ArrayList<String> stringArrayList = new ArrayList<>();
                stringArrayList.add(c.getString(0));
                stringArrayList.add(c.getString(1).substring(0, 10));
                columnsList.add(stringArrayList);
                c.moveToNext();
            }
        }
        c.close();
        return columnsList;
    }

    /**
     * Removes all entries associated with a run number from the table, starting on a certain date
     * @param run Run number to remove
     * @param date Date to remove runs from
     */
    public void deleteRun(int run, String date) {
        db.delete("tasksTable","run=? and date > Datetime(?)",new String[]{Integer.toString(run),date});
    }

    /**
     * Empties the queue of rows to be deleted
     */
    public void emptyDeleteQueue(ArrayList<String> deleteQueue) {
        for (String table_id : deleteQueue) {
            System.out.println("Deleting " + table_id);
            db.delete("tasksTable", "unique_table_id=" + table_id, null);
        }
    }
}
