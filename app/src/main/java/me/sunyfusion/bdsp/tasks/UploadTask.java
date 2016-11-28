package me.sunyfusion.bdsp.tasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import me.sunyfusion.bdsp.Utils;
import me.sunyfusion.bdsp.db.BdspDB;
import me.sunyfusion.bdsp.state.Global;

/**
 * Task that posts a list of JSONArrays to a given URL
 * Created by jesse on 8/1/16.
 */
public class UploadTask extends AsyncTask<Void, Void, ArrayList<JSONArray>> {
    private BdspDB db = Global.getDb();                     //gets an instance of db from Global singleton
    private Context context;
    final String submitUrl;                                 //URL to post JSONArrays to
    ArrayList<String> deleteQueue = new ArrayList<String>();    //Queue containing the IDs of database entries that have been successfully submitted

    //Constructs an UploadTask that posts to specified URL using specified context
    public UploadTask(Context c, String url) {
        super();
        context = c;
        submitUrl = url;
    }

    @Override
    protected ArrayList<JSONArray> doInBackground(Void... voids) {
        if(db != null) {                                                //check for null pointer
            ArrayList<JSONArray> jsonArrayList = new ArrayList<JSONArray>();
            Cursor records;
            try {
                records = db.queueAll(null);                                  //gets a cursor to all records in the db
            }
            catch(NullPointerException e) {
                System.out.println("Failing upload gracefully");
                return null;
            }
            JSONArray jsonArray;
            JSONObject jsonObject;
            if (records.getCount() == 0)                                      //Check if database is empty
                return null;                                                  //if it is do nothing
            records.moveToNext();                                             //Steps to first record in cursor
            String[] cNames = records.getColumnNames();

            int cCount = records.getColumnCount();
            while (!records.isAfterLast()) {                                  //while there are records to process
                jsonArray = new JSONArray();
                jsonObject = new JSONObject();
                for (int i = 0; i < cCount; i++) {                      //for all columns in a record
                    if (!cNames[i].equals("unique_table_id"))           //if they are not the PK
                        try {
                            jsonObject.put(cNames[i], records.getString(i));  //add the column to the JSON Object
                        } catch (JSONException e) {
                            Log.d("BDSP", "JSONexception");
                        }
                }
                jsonArray.put(jsonObject);                                      //add the JSON Object to JSON Array;
                jsonArrayList.add(jsonArray);                                   //add the JSON Array to the ArrayList
                deleteQueue.add(records.getString(0));                        //queue that record for deletion by PK
                records.moveToNext();                                         //go to the next record
            }
            records.close();                                                  //close the cursor (clean up)
            return jsonArrayList;
        }
        else return null;
    }

    @Override
    protected void onPostExecute(ArrayList<JSONArray> jsonArrays) {
        super.onPostExecute(jsonArrays);
        if(jsonArrays != null) {                                                 //if the JSONArray List is not empty
            for (JSONArray jsonArray : jsonArrays) {                            //For each JSONArray
                doRowUpload(context, submitUrl, jsonArray, null);                        //Post it to the submitUrl
            }
        }
        File[] fileList = Utils.getPhotoList(Global.getContext());              //Get a list of all photos taken
        for(File f : fileList) {                                                //For each image
            doImageUpload(context, "http://sunyfusion.me/ft_test/photos.php", f);        //Upload it to the URL
        }
    }

    /**
     * Creates and sends an HTTP post request to the server, which includes the image captured
     * from the getImage() method. Also adds the HTTP response from the server to the UI. Uses
     * the Android Async HTTP Library that can be found at http://loopj.com/android-async-http/
     */
    private boolean doRowUpload(final Context c, String url, JSONArray jsonParams, Uri imgUri) {
        AsyncHttpClient client = new AsyncHttpClient();
        final boolean status = false;
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());                   //convert the jsonArray to something we can use
        } catch (Exception e) {
            System.out.println(e);
        }
        //TODO move auth information to a file that is not on github, then change the auth info
        client.setBasicAuth("SUNY", "GreenTreeTables");     //sets user and password for basic HTTP auth
        client.setTimeout(20000);
        client.setMaxRetriesAndTimeout(0, 1);
        client.post(c, url, entity, "application/json", new FileAsyncHttpResponseHandler(c) {       //posts entity to url as type json

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                Toast.makeText(c, "Success " + statusCode, Toast.LENGTH_LONG).show();               //show a toast that the submission was successful
                Log.i("UPLOAD", "SUCCESS");
                new EmptyDeleteQueueTask().execute(deleteQueue);                                    //deletes successfully submitted records
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, File errorResponse) {
                Toast.makeText(c, "Failed " + statusCode, Toast.LENGTH_LONG).show();                //show error toast
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
        return status;
    }
    private boolean doImageUpload(final Context c, final String url, final File fileToUpload) {
        AsyncHttpClient client = new AsyncHttpClient();
        final boolean status = false;
        RequestParams req = new RequestParams();
        try {
            req.put("photo", fileToUpload);                                                         //add file to ReqParams
            //TODO make this wrk off the config file
            req.put("projectName", "assetTest");              //projectName specifies the folder the photo will be added to
        }
        catch(FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        //post test
        client.setBasicAuth("SUNY", "GreenTreeTables");                                             //configure basic auth with server
        client.setTimeout(20000);
        client.setMaxRetriesAndTimeout(0, 1);
        client.post(url, req, new FileAsyncHttpResponseHandler(c) {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                Toast.makeText(c, "Photo Success " + statusCode, Toast.LENGTH_LONG).show();         //show toast
                Log.i("UPLOAD", "SUCCESS");
                Utils.deletePhoto(Global.getContext(), fileToUpload);                               //delete file from phone
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, File errorResponse) {
                Toast.makeText(c, "Photo Failed " + statusCode, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
        return status;
    }
}