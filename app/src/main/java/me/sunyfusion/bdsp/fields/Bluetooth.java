package me.sunyfusion.bdsp.fields;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import me.sunyfusion.bdsp.BdspRow;
import me.sunyfusion.bdsp.R;
import me.sunyfusion.bdsp.adapter.FieldAdapter;
import me.sunyfusion.bdsp.bluetooth.BluetoothConnection;
import me.sunyfusion.bdsp.bluetooth.Constants;
import me.sunyfusion.bdsp.state.Global;

/**
 * Created by deisingj1 on 11/7/2016.
 */

public class Bluetooth implements Field {

    final int containerId = R.id.bluetoothView;             //id of the top level LinearLayout
    final int labelId = R.id.bluetoothLabel;                //id of the label
    final int valueId = R.id.bluetoothValue;                //id of the view that contains the value
    final int buttonId = R.id.bluetoothButton;              //id of the button

    private String label = "";
    private View thisView;
    Context context;
    public Bluetooth(Context c, String l) {
        context = c;
        label = l;
    }

    /**
     * @return Label of the field
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return The view associated with this field
     */
    public View getView() {
        return thisView;
    }

    /**
     * Reset the field
     */
    public void clearField() {
        ((EditText) thisView.findViewById(valueId)).setText("");
    }

    /**
     * Make and set up the field, showing the correct field
     * @param holder The viewholder that contains the view to be inflated
     * @return True if successful, false otherwise
     */
    public boolean makeField(FieldAdapter.ViewHolder holder) {
        Button button = (Button) holder.mView.findViewById(buttonId);
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
                        new BluetoothConnection().execute();
                    }
                }
                else {
                    new AlertDialog.Builder(Global.getContext())
                            .setTitle(Constants.ERROR_TITLE)
                            .setMessage(Constants.ERROR_MESSAGE_00)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) { }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .show();
                }
            }
        });
        thisView = holder.mView.findViewById(containerId);
        holder.mView.findViewById(containerId).setVisibility(View.VISIBLE);
        final TextView t = (TextView) holder.mView.findViewById(labelId);
        t.setText(getLabel());
        EditText e = (EditText) holder.mView.findViewById(valueId);
        e.addTextChangedListener(new TextWatcher() {                    //Updates the value of the var in this object
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                BdspRow.getInstance().put(t.getText().toString(), s.toString());
            }
        });
        return true;
    }
}
