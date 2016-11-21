package me.sunyfusion.bdsp.fields;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import me.sunyfusion.bdsp.BdspRow;
import me.sunyfusion.bdsp.R;
import me.sunyfusion.bdsp.adapter.UniqueAdapter;

/**
 * Created by deisingj1 on 11/7/2016.
 */

public class Bluetooth implements Field {

    final int containerId = R.id.bluetoothView;
    final int labelId = R.id.bluetoothLabel;
    final int valueId = R.id.bluetoothValue;

    private String label = "";
    private String[] sArray;
    private View thisView;
    Context context;
    public Bluetooth(Context c, String l) {
        context = c;
        label = l;
    }
    public String getLabel() {
        return label;
    }

    public View getView() {
        return thisView;
    }
    public void clearField() {
        ((EditText) thisView.findViewById(valueId)).setText("");
    }

    public boolean makeField(UniqueAdapter.ViewHolder holder) {
        thisView = holder.mView.findViewById(containerId);
        holder.mView.findViewById(containerId).setVisibility(View.VISIBLE);
        final TextView t = (TextView) holder.mView.findViewById(labelId);
        t.setText(getLabel());
        EditText e = (EditText) holder.mView.findViewById(valueId);
        e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                BdspRow.getInstance().put(t.getText().toString(), s.toString());
            }
        });
        return true;
    }
}
