package me.sunyfusion.bdsp.fields;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import me.sunyfusion.bdsp.BdspRow;
import me.sunyfusion.bdsp.R;
import me.sunyfusion.bdsp.adapter.UniqueAdapter;
import me.sunyfusion.bdsp.state.Global;

/**
 * Created by deisingj1 on 11/14/2016.
 */

public class Dropdown implements Field {

    final int containerId = R.id.dropdownView;
    final int labelId = R.id.dropdownLabel;
    final int valueId = R.id.dropdownValue;

    private View thisView;
    private String label = "";
    private String[] sArray;
    Context context;
    public Dropdown(Context c, String l) {
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
        ((Spinner) thisView.findViewById(valueId)).setSelection(0);
    }
    public String[] getArray() {
        return sArray;
    }
    public void setArray(String[] array) {
        sArray = array;
    }

    public boolean makeField(UniqueAdapter.ViewHolder holder) {
        thisView = holder.mView.findViewById(containerId);
        holder.mView.findViewById(containerId).setVisibility(View.VISIBLE);
        final TextView t = (TextView) holder.mView.findViewById(labelId);
        t.setText(getLabel());
        Spinner s = (Spinner) holder.mView.findViewById(valueId);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Global.getContext(), android.R.layout.simple_spinner_item, getArray());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BdspRow.getInstance().put(t.getText().toString(), parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return true;
    }
}
