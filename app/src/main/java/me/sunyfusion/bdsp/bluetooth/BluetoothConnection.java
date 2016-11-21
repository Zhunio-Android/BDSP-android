package me.sunyfusion.bdsp.bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import me.sunyfusion.bdsp.activity.MainActivity;
import me.sunyfusion.bdsp.state.Global;

/**
 * This class extends AsyncTask.
 *
 * It creates a server socket, and once a connection to a Bluetooth device
 * is accepted, it manages the connection by receiving data. Once all data has been
 * read, it is displayed via population into a text field within the UI thread.
 *
 * Works in conjunction with Constants.java to communicate progress to the user.
 * Includes error handling.
 *
 * Created by Bryan R Martinez on 11/16/2016.
 */
public class BluetoothConnection extends AsyncTask<Void, Integer, Integer> {
    private BluetoothServerSocket bluetoothServerSocket;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private StringBuffer output;
    private View view;

    @Override
    protected void onPreExecute() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothServerSocket = null;
        bluetoothSocket = null;
        inputStream = null;
        output = new StringBuffer();
        view = ((MainActivity) Global.getContext()).getView("Thing");

        try {
            bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                    Constants.SERVICE_NAME, UUID.fromString(Constants.UUID));
        } catch (IOException e) { }
    }

    @Override
    protected Integer doInBackground(Void... params) {
        if (bluetoothServerSocket != null)
            try {
                publishProgress(Constants.WAITING_FOR_CLIENT);
                bluetoothSocket = bluetoothServerSocket.accept(30000); // 30 second timeout
                cancelBTServerSocket();
                if (bluetoothSocket != null)
                    try {
                        inputStream = bluetoothSocket.getInputStream();
                        if (inputStream != null) {
                            byte[] buffer = new byte[2056];
                            int bytes;

                            while (true) {
                                try {
                                    bytes = inputStream.read(buffer);
                                    String s = new String(buffer, 0, bytes);
                                    output.append(s);
                                } catch (IOException e) {
                                    publishProgress(Constants.MESSAGE_READ_COMPLETE);
                                    break;
                                }
                            }

                            return Constants.SUCCESS;
                        }
                    } catch (IOException e) {
                        publishProgress(Constants.ERROR);
                        cancelBTSocket();
                    }
            } catch (IOException e) {
                publishProgress(Constants.ERROR_TIMEOUT);
                cancelBTServerSocket();
            }
        else { publishProgress(Constants.ERROR); }
        return Constants.ERROR;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        switch (values[0]) {
            case Constants.WAITING_FOR_CLIENT:
                Toast.makeText((Global.getContext()),
                        Constants.WAITING_MESSAGE, Toast.LENGTH_LONG).show();
                break;
            case Constants.MESSAGE_READ_COMPLETE:
                Toast.makeText((Global.getContext()),
                        Constants.READ_COMPLETE_MESSAGE, Toast.LENGTH_LONG).show();
                break;
            case Constants.ERROR:
                new AlertDialog.Builder(Global.getContext())
                        .setTitle(Constants.ERROR_TITLE)
                        .setMessage(Constants.ERROR_MESSAGE_01)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) { }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
                break;
            case Constants.ERROR_TIMEOUT:
                new AlertDialog.Builder(Global.getContext())
                        .setTitle(Constants.ERROR_TITLE)
                        .setMessage(Constants.ERROR_MESSAGE_02)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) { }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            default:
                break;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        cancelBTSocket();
        if (result == Constants.SUCCESS) {
            if (view != null) {
                ((EditText) view).setText(output.toString());
            }
        }
    }

    private void cancelBTServerSocket() {
        if (bluetoothServerSocket != null) {
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) { }
        }
    }

    private void cancelBTSocket() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) { }
        }
    }
}
