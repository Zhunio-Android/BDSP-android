package me.sunyfusion.fuzion.column;

import android.content.Context;

/**
 * Created by deisingj1 on 8/4/2016.
 */
public class Location extends Column{
    String latColumn, lonColumn;

    public Location(Context c, String s, String latCol, String lonCol) {
        super(c,s);
    }
}
