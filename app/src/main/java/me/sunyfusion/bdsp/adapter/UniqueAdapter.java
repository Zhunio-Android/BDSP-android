package me.sunyfusion.bdsp.adapter;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import me.sunyfusion.bdsp.BdspRow;
import me.sunyfusion.bdsp.R;
import me.sunyfusion.bdsp.Unique;
import me.sunyfusion.bdsp.activity.MainActivity;
import me.sunyfusion.bdsp.bluetooth.Constants;
import me.sunyfusion.bdsp.bluetooth.ServerConnection;
import me.sunyfusion.bdsp.state.Global;

/**
 * @author Jesse Deisinger
 * @version 7.13.16
 */

public class UniqueAdapter extends RecyclerView.Adapter<UniqueAdapter.ViewHolder> {
    /**
     * Holds list of Unique objects created when BdspConfig.init was run
     */
    private ArrayList<Unique> uniqueList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public UniqueAdapter(ArrayList<Unique> myDataset) {
        uniqueList = myDataset;
    }

    @Override
    public UniqueAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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
        Unique unique = uniqueList.get(position);
        switch(unique.getType()) {
            case "textfield":
                makeTextField(holder, unique);
                break;
            case "spinner":
                makeSpinner(holder, unique);
                break;
            case "location":
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return uniqueList.size();
    }

    private boolean makeTextField(ViewHolder holder, Unique u) {
        holder.mView.findViewById(R.id.textField).setVisibility(View.VISIBLE);
        final TextView t = (TextView) holder.mView.findViewById(R.id.uniqueName);
        t.setText(u.getText());
        holder.mView.findViewById(R.id.textField).setVisibility(View.VISIBLE);
        Button button = (Button) holder.mView.findViewById(R.id.button_bt_server);
        button.setText(Constants.BUTTON_TEXT);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter != null) {
                    if (!bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.enable();
                        Toast.makeText(Global.getContext(), Constants.ENABLED, Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        ServerConnection serverConnection = new ServerConnection();
                        serverConnection.start();
                    }
                }
                else {
                    MainActivity.handler.obtainMessage(Constants.NO_BT_ADAPTER).sendToTarget();
                }
            }
        });
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
    private boolean makeSpinner(ViewHolder holder, Unique u) {
        holder.mView.findViewById(R.id.spinnerLayout).setVisibility(View.VISIBLE);
        final TextView t = (TextView) holder.mView.findViewById(R.id.spinnerName);
        t.setText(u.getText());
        Spinner s = (Spinner) holder.mView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Global.getContext(), android.R.layout.simple_spinner_item, u.getArray());
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
    private boolean makePhoto() {
        return false;
    }
}


