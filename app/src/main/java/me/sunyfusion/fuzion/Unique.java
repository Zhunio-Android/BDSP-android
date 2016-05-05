package me.sunyfusion.fuzion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by jesse on 5/4/16.
 */
public class Unique {

    private LinearLayout box;
    private EditText t;
    private String name;
    public Unique(Context c, String[] args) {
        name = args[1];
        box = new LinearLayout(c);   // layout to wrap the whole thing
        box.setOrientation(LinearLayout.VERTICAL);
        box.setBackgroundColor(Color.BLACK);    // Can use transparent if you want for the background color.
        box.setPadding(0, 10, 0, 10);

        LinearLayout l = new LinearLayout(c);   // layout for the text entry and the enter button
        l.setOrientation(LinearLayout.HORIZONTAL);

        t = new EditText(c);    // makes the edit text field

        TextView uniqueText = new TextView(c);   // Unique label
        uniqueText.setText(name);
        uniqueText.setBackgroundColor(Color.TRANSPARENT);
        uniqueText.setTextColor(Color.WHITE);
        uniqueText.setPadding(15, 0, 0, 0);


        l.addView(t);

        t.setSingleLine();
        t.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        box.addView(uniqueText);
        box.addView(l);
    }
    public String getText() {
        return t.getText().toString();
    }
    public LinearLayout getView() {
        return box;
    }
    public String getName(){
        return name;
    }
    public void clearText() {
        t.getText().clear();
    }
}
