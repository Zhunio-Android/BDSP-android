package me.sunyfusion.bdsp.bluetooth;

/**
 * This class contains constants which are used for
 * Bluetooth related operations.
 *
 * Created by Bryan R Martinez on 11/10/2016.
 */
public class Constants {
    public static final String SERVICE_NAME = "Bluetooth Transfer";
    public static final String UUID = "f1239387-98b2-4dce-a7d5-635ce03572a0"; // Custom UUID
    public static final String BUTTON_TEXT = "Get Data";

    // Handler message options
    public static final int NO_BT_ADAPTER = 0;
    public static final int WAITING_FOR_CLIENT = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_READ_COMPLETE = 3;
    public static final int ERROR = 4;

    // User communication messages
    public static final String ENABLED = "Bluetooth enabled";
    public static final String READ_COMPLETE = "Successfully read data";
    public static final String ALERT_TITLE = "Alert";
    public static final String WAITING_MESSAGE = "Waiting for client connection";
    public static final String ERROR_TITLE = "Error";
    public static final String ERROR_MESSAGE_01 = "Device does not support Bluetooth";
    public static final String ERROR_MESSAGE_02 = "Unable to establish Bluetooth connection";
}
