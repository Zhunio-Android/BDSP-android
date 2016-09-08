package me.sunyfusion.bdsp.state;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import me.sunyfusion.bdsp.db.BdspDB;

/**
 * Created by jesse on 8/1/16.
 */
public class Global {
    private Context context;
    private static Global ourInstance = new Global();
    private Config config;

    BdspDB dbHelper;
    SQLiteDatabase db;

    public static Global getInstance() {
        return ourInstance;
    }
    public static Config getConfig() { return getInstance().config; }

    private Global() {

    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        dbHelper = new BdspDB(context);
    }
    public void setConfig(Config config) {
        this.config = config;
    }

    public static BdspDB getDb() {
        return getInstance().dbHelper;
    }

    public static Context getContext() {
        return getInstance().context;
    }
}
