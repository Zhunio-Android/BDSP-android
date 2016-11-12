package me.sunyfusion.bdsp.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

import me.sunyfusion.bdsp.activity.MainActivity;

/**
 * This class creates a Bluetooth server socket, allowing
 * Bluetooth devices with knowledge of this application's
 * UUID to send data to this device.
 *
 * Instantiates a class to manage connection upon successful
 * acceptance of client's request to initiate communication.
 *
 * Created by Bryan R Martinez on 11/10/2016.
 */
public class ServerConnection extends Thread {
    private BluetoothServerSocket bluetoothServerSocket;

    public ServerConnection() {
        bluetoothServerSocket = null;
        try {
            bluetoothServerSocket = BluetoothAdapter.getDefaultAdapter()
                    .listenUsingRfcommWithServiceRecord(Constants.SERVICE_NAME,
                            UUID.fromString(Constants.UUID));
        } catch (IOException e) {
            MainActivity.handler.obtainMessage(Constants.ERROR).sendToTarget();
        }
    }

    @Override
    public void run() {
        BluetoothSocket bluetoothSocket = null;
        if (bluetoothServerSocket != null) {
            try {
                MainActivity.handler.obtainMessage(Constants.WAITING_FOR_CLIENT).sendToTarget();
                bluetoothSocket = bluetoothServerSocket.accept(30000); // 30 seconds to connect
            } catch (IOException e) {
                MainActivity.handler.obtainMessage(Constants.ERROR).sendToTarget();
                cancel();
            }
            if (bluetoothSocket != null) {
                ManageConnection manageConnection = new ManageConnection(bluetoothSocket);
                manageConnection.start();
                cancel();
            }
        }
    }

    private void cancel() {
        if (bluetoothServerSocket != null) {
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) { }
        }
    }
}
