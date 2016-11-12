package me.sunyfusion.bdsp.bluetooth;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;

import me.sunyfusion.bdsp.activity.MainActivity;

/**
 * This class accepts a socket, and manages interactions with
 * that connected device. In this case, the purpose is to read
 * from the input stream and display received data via
 * communication with a Handler.
 *
 * Created by Bryan R Martinez on 11/10/2016.
 */
public class ManageConnection extends Thread {
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;

    public ManageConnection(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
        inputStream = null;

        if (bluetoothSocket != null) {
            try {
                inputStream = bluetoothSocket.getInputStream();
            } catch (IOException e) {
                cancel();
            }
        }
    }

    @Override
    public void run() {
        if (inputStream != null) {
            byte[] buffer = new byte[2056];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    MainActivity.handler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    MainActivity.handler.obtainMessage(Constants.MESSAGE_READ_COMPLETE)
                            .sendToTarget();
                    break;
                }
            }

            cancel();
        }
        else {
            MainActivity.handler.obtainMessage(Constants.ERROR).sendToTarget();
        }
    }

    private void cancel() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) { }
        }
    }
}
