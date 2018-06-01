package org.bd_sp.bdsp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import org.bd_sp.bdsp.R;
import org.bd_sp.bdsp.fields.Field;

/**
 * @author Jesse Deisinger
 * @version 7.13.16
 */

/**
 *  Allows fields to be inserted into the RecyclerView, helps to build the UI
 */
public class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.ViewHolder> {
    /**
     * Holds list of Text objects created when BdspConfig.init was run
     */
    private ArrayList<Field> fieldList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public FieldAdapter(ArrayList<Field> myDataset) {
        fieldList = myDataset;
    }

    @Override
    public FieldAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int p = position;
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Field field = fieldList.get(position);
        field.makeField(holder);
    }

    @Override
    public int getItemCount() {
        return fieldList.size();
    }

}

