package me.sunyfusion.fuzion.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SaveReceiver extends BroadcastReceiver {
    public SaveReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        System.out.println("SAVED COLUMNS?");
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
