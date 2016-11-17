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
    public static final int WAITING_FOR_CLIENT = 0;
    public static final int MESSAGE_READ_COMPLETE = 1;
    public static final int SUCCESS = 2;
    public static final int ERROR = 3;
    public static final int ERROR_TIMEOUT = 4;

    // User communication messages
    public static final String ENABLED = "Bluetooth enabled";
    public static final String WAITING_MESSAGE = "Waiting for client connection (30s)";
    public static final String READ_COMPLETE_MESSAGE = "Successfully read data";

    public static final String ERROR_TITLE = "Error";
    public static final String ERROR_MESSAGE_00 = "Device does not support Bluetooth";
    public static final String ERROR_MESSAGE_01 = "Unable to establish Bluetooth connection";
    public static final String ERROR_MESSAGE_02 = "Connection timed out, try again";
}
