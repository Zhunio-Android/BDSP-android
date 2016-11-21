package me.sunyfusion.bdsp.fields;

import android.content.Context;

import me.sunyfusion.bdsp.BdspRow;
import me.sunyfusion.bdsp.Utils;
import me.sunyfusion.bdsp.db.BdspDB;

/**
 * Created by deisingj1 on 11/17/2016.
 */

public class FieldFactory {
    private static BdspDB db;
    private static void addColumn(Context c, BdspRow.ColumnType type, String name) {
        if (db == null) {
            db = new BdspDB(c);
        }
        db.addColumn(name,"TEXT");
        BdspRow.ColumnNames.put(type,name);
    }
    public static Field build(Context c, String[] desc) {
        Field f;
        addColumn(c, BdspRow.ColumnType.UNIQUE, desc[1]);
        switch(desc[0]) {
            case "photo":
                addColumn(c, BdspRow.ColumnType.PHOTO, desc[1]);
                f = new Camera(c, desc[1]);
                break;
            case "textfield":
                f = new Text(c, desc[1]);
                break;
            case "dropdown":
                f = new Dropdown(c, desc[1]);
                ((Dropdown) f).setArray(Utils.stringToArray(desc[2]));
                break;
            case "bluetooth":
                f = new Bluetooth(c, desc[1]);
                break;
            default:
                f = new Text(c, "null");
                break;
        }
        return f;
    }
}
