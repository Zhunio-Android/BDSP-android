package me.sunyfusion.fuzion;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by jesse on 4/19/16.
 */
public class Layout {
    public static View createActionBar(Context c, ActionBar a){
        LinearLayout a_view = new LinearLayout(c);
        a_view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        a_view.setOrientation(LinearLayout.HORIZONTAL);
        a_view.setGravity(Gravity.CENTER);
        a.setCustomView(a_view);
        a.setDisplayShowCustomEnabled(true);
        return a_view;
    }
    public static View createMainLayout(Context c) {
        LinearLayout layout = new LinearLayout(c);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
     //   layout.setBackgroundColor(Color.CYAN);
     //   layout.setBackgroundColor(Color.CYAN);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }
    public static ScrollView makeScroll(Context c, View l) {
        ScrollView scroll = new ScrollView(c);
        scroll.setBackgroundColor(Color.TRANSPARENT);
        scroll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scroll.addView(l);
        return scroll;
    }
}
