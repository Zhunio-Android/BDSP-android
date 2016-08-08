package me.sunyfusion.fuzion.column;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import me.sunyfusion.fuzion.db.BdspDB;
import me.sunyfusion.fuzion.state.Global;

/**
 * Created by deisingj1 on 8/4/2016.
 */
public abstract class Column {
    private String columnName;
    private Context c;
    private String value = "";
    private BdspDB db;
    final Column column = this;

    public Column(Context context, String colName) {

        c = context;
        columnName = colName;
        db = Global.getDb();
        db.addColumn(colName, "TEXT");
        LocalBroadcastManager.getInstance(c).registerReceiver(receiver, new IntentFilter("save-all-columns"));
    }

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

    public void insertValue(ContentValues v) {
        System.out.println("Superclass insval");
        v.put(columnName,value);
    }

    public String getColumnName() {
        return columnName;
    }
    public void setValue(String v) {
        value = v;
    }
    public String getValue() { return value; }

}
