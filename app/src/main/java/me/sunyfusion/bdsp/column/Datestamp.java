package me.sunyfusion.bdsp.column;

import android.content.ContentValues;
import android.content.Context;

import me.sunyfusion.bdsp.Utils;
import me.sunyfusion.bdsp.db.BdspDB;

/**
 * Created by jesse on 5/2/16.
 */
public class Datestamp extends Column {

    public Datestamp(Context c, String name, BdspDB db) {
        super(c,name,db);
    }

    @Override
    public void insertValue(ContentValues v) {
        String cdt = Utils.getDateString("yyyy-MM-dd H:m:s");
        v.put(getName(), cdt);
    }

}
