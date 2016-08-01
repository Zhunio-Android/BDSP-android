package me.sunyfusion.fuzion;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jesse on 7/12/16.
 */

public class UniqueAdapter extends RecyclerView.Adapter<UniqueAdapter.ViewHolder> {
    private ArrayList<Unique> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public UniqueAdapter(ArrayList<Unique> myDataset) {
        mDataset = myDataset;
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
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView t = (TextView) holder.mView.findViewById(R.id.uniqueName);
        t.setText(mDataset.get(position).toString());

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}


