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
    private BdspDB self;

    public BdspDB(Context c)
    {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
        //db = this.getWritableDatabase();
        self = this;
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
     * Inserts values into database
     * @param values values to be inserted
     */
    public long insert(ContentValues values) {
        SQLiteDatabase db = self.getWritableDatabase();
        long status = db.insert(TABLE_NAME,null,values);
        db.close();
        return status;
    }

    /**
     * Adds a column to the database
     * @param columnName name of the column
     * @param columnType type of the column
     */
    public void addColumn(String columnName, String columnType)
    {
        SQLiteDatabase db = self.getWritableDatabase();
        try {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + columnName + " " + columnType);
        }
        catch(SQLiteException e) {
            System.out.println(e.getMessage());
        }
        db.close();
    }
    /**
     * Queries the default table, returning a cursor to every row in the table
     * @return a cursor to all of the rows in the table
     */
    public Cursor queueAll(Object[] o) {
        SQLiteDatabase db = self.getWritableDatabase();
        Cursor cursor;
        if (o != null) {
            String[] s = new String[]{(String) o[0], (String) o[1] + "%"};
            cursor = db.query("tasksTable", null, "run=? and date LIKE ?",
                    s, null, null, null);
        } else {
            cursor = db.query("tasksTable", null, null,
                    null, null, null, null);
        }
        return cursor;
    }
    /**
     * Removes all entries associated with a run number from the table, starting on a certain date
     * @param run Run number to remove
     * @param date Date to remove runs from
     */
    public void deleteRun(int run, String date) {
        SQLiteDatabase db = self.getWritableDatabase();
        db.delete("tasksTable","run=? and date > Datetime(?)",new String[]{Integer.toString(run),date});
        db.close();
    }
    /**
     * Empties the queue of rows to be deleted
     */
    public void emptyDeleteQueue(ArrayList<String> deleteQueue) {
        SQLiteDatabase db = self.getWritableDatabase();
        for (String table_id : deleteQueue) {
            System.out.println("Deleting " + table_id);
            db.delete("tasksTable", "unique_table_id=" + table_id, null);
        }
        db.close();
    }
}
