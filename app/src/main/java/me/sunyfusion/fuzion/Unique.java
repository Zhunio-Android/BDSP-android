package me.sunyfusion.fuzion;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by jesse on 5/4/16.
 */
public class Unique {

    private String name;
    public Unique(Context c, String[] args) {
        name = args[1];
    }

    public String toString() {
        return name;
    }

    public String getName(){
        return name;
    }
}
