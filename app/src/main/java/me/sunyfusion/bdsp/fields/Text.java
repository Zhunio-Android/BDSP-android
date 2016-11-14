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

public class Text implements Field {
    private String label = "";
    private String[] sArray;
    Context context;
    public Text(Context c, String l) {
        context = c;
        label = l;
    }
    public String getLabel() {
        return label;
    }
    public String[] getArray() {
        return sArray;
    }
    public void setArray(String[] array) {
        sArray = array;
    }

    public boolean makeField(UniqueAdapter.ViewHolder holder) {
        holder.mView.findViewById(R.id.textField).setVisibility(View.VISIBLE);
        final TextView t = (TextView) holder.mView.findViewById(R.id.uniqueName);
        t.setText(getLabel());
        holder.mView.findViewById(R.id.textField).setVisibility(View.VISIBLE);
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
                BdspRow.getInstance().put(t.getText().toString(), s.toString());
            }
        });
        return true;
    }
}
