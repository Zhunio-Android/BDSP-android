package me.sunyfusion.bdsp.fields;

import android.content.Context;

import me.sunyfusion.bdsp.BdspRow;
import me.sunyfusion.bdsp.Utils;
import me.sunyfusion.bdsp.db.BdspDB;

/**
 * Creates Fields based on input from config file
 */
public class FieldFactory {
    private static BdspDB db;

    /**
     * Add column to database
     * @param c
     * @param type
     * @param name
     */
    private static void addColumn(Context c, BdspRow.ColumnType type, String name) {
        if (db == null) {
            db = new BdspDB(c);
        }
        db.addColumn(name,"TEXT");
        BdspRow.ColumnNames.put(type,name);
    }

    /**
     * Creates a field based on the description provided
     * @param c Context to use
     * @param desc Description of the field to create from the config field
     * @return
     */
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
