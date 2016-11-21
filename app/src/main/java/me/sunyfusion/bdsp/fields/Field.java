package me.sunyfusion.bdsp.fields;

import android.view.View;

import me.sunyfusion.bdsp.adapter.UniqueAdapter;

/**
 * Created by deisingj1 on 11/14/2016.
 */

public interface Field {
    String label = "";
    int labelId = 0;
    int valueId = 0;

    /**
     * gets the string assigned to the field label
     * @return String represenation of the field label
     */
    String getLabel();

    /**
     * gets the view that contains the label and data for a field
     * @return view that contains field label and data
     */
    View getView();

    /**
     * clears the contents of the data portion of the field
     */
    void clearField();

    /**
     * Helps inflate a new field
     * @param holder The viewholder that contains the view to be inflated
     * @return true if successful, false if not successful
     */
    boolean makeField(UniqueAdapter.ViewHolder holder);
}
