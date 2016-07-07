package me.sunyfusion.fuzion;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Robert Wieland on 3/26/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Tasks.db";
    public static final String TABLE_NAME = "tasksTable";
    public static SQLiteDatabase db;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + TABLE_NAME+" (unique_table_id INTEGER PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    // Method will create new columns in the table of the database
    public void addColumn(SQLiteDatabase db, String columnName, String columnType)
    {
        try {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + columnName + " " + columnType);
        }
        catch(SQLiteException e) {}
    }
    public Cursor queueAll(SQLiteDatabase db) {
        String[] columns = new String[] { "*" };

        Cursor cursor = db.query("tasksTable", null, null,
                null, null, null, null);
        return cursor;
    }

    public static SQLiteDatabase getCurrentDB() {
        return db;
    }
}
