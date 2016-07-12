package me.sunyfusion.fuzion;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by jesse on 7/12/16.
 */
public class UniqueAdapter extends ArrayAdapter<Unique> {
    private List<Unique> uniqueList;
    private Context context;

    public UniqueAdapter(List<Unique> uniques, Context c) {
        super(c, R.layout.row_layout, uniques);
        this.uniqueList = uniques;
        this.context = c;
    }
}
