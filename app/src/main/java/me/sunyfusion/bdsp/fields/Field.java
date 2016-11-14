package me.sunyfusion.bdsp.fields;

import me.sunyfusion.bdsp.adapter.UniqueAdapter;

/**
 * Created by deisingj1 on 11/14/2016.
 */

public interface Field {
    String label = "";
    String getLabel();
    boolean makeField(UniqueAdapter.ViewHolder holder);
}
