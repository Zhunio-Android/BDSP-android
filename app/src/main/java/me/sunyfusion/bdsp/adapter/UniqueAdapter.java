package me.sunyfusion.bdsp.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import me.sunyfusion.bdsp.R;
import me.sunyfusion.bdsp.column.Column;

/**
 * @author Jesse Deisinger
 * @version 7.13.16
 */

public class UniqueAdapter extends RecyclerView.Adapter<UniqueAdapter.ViewHolder> {
    /**
     * Holds list of Unique objects created when Config.init was run
     */
    private ArrayList<Column> uniqueList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public UniqueAdapter(ArrayList<Column> myDataset) {
        uniqueList = myDataset;
    }

    @Override
    public UniqueAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int p = position;
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView t = (TextView) holder.mView.findViewById(R.id.uniqueName);
        t.setText(uniqueList.get(position).getName());
        EditText e = (EditText) holder.mView.findViewById(R.id.uniqueValue);
        e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                uniqueList.get(p).setValue(s.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return uniqueList.size();
    }
}


