package me.sunyfusion.bdsp.tasks;

import android.os.AsyncTask;

import me.sunyfusion.bdsp.state.Global;

/**
 * Created by deisingj1 on 9/8/2016.
 */
public class EmptyDeleteQueueTask extends AsyncTask<Void,Void,Void>{
    @Override
    protected Void doInBackground(Void... params) {
        Global.getDb().emptyDeleteQueue();
        return null;
    }
}
