package me.sunyfusion.bdsp.tasks;

import android.os.AsyncTask;

import java.util.ArrayList;

import me.sunyfusion.bdsp.state.Global;

/**
 * Created by deisingj1 on 9/8/2016.
 */
public class EmptyDeleteQueueTask extends AsyncTask<ArrayList<String>,Void,Void>{
    @Override
    protected Void doInBackground(ArrayList<String>... deleteLists) {
        for(ArrayList<String> list : deleteLists) {
            Global.getDb().emptyDeleteQueue(list);
        }
        return null;
    }
}
