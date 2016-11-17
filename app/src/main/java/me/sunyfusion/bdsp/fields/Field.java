package me.sunyfusion.bdsp.fields;

import me.sunyfusion.bdsp.adapter.UniqueAdapter;

/**
 * Created by deisingj1 on 11/14/2016.
 */

public interface Field {
    String label = "";
    int labelId = 0;
    int valueId = 0;
    String getLabel();
    boolean makeField(UniqueAdapter.ViewHolder holder);
}
