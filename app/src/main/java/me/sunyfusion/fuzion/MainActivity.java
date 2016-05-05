//TODO have datatypes option for unique fields - shelved
//TODO autoincrement field
//TODO section function - auto increment by date on field
//TODO flagged fields - allow binary
//TODO manual or incremental field - trigger = field, reset = how you want to reset (daily or no reset)
//TODO housekeeping, separate manual and automatically collected

//TODO read run from input file, reset run to 0 at midnight
//TODO add fields for GPS feedback
//TODO possible background service for GPS tracking


package me.sunyfusion.fuzion;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    // CONSTANTS
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    //private static final String SUBMIT_URL = "https://sunyfusion-franzvz.c9users.io/ft_test/update.php";
    private static String SUBMIT_URL = "http://www.sunyfusion.me/ft_test/update.php";

    //GLOBAL VARS
    static Uri imgUri;
    static boolean sendGPS = false;
    static boolean sendRun = false;
    double latitude = -1;
    double longitude = -1;
    double gps_acc = 1000;
    int GPS_FREQ = 10000;
    static String id_value;
    static String id_key;
    static String gps_tracker_lat;
    static String gps_tracker_long;
    EditText idTxt;
    LocationManager locationManager;
    LinearLayout.LayoutParams defaultLayoutParams;
    LinearLayout.LayoutParams scrollParameters;
    static DatabaseHelper dbHelper;
    static SQLiteDatabase db;
    static HTTPFunc httpFunc;
    PowerManager.WakeLock wakelock;
    DateHelper date;
    ContentValues values;

    //LAYOUTS
    private static LinearLayout layout;
    private static LinearLayout a_view;
    private static LinearLayout mainFrame;
    private static RelativeLayout logoLayout;

    //GLOBAL BOOLEANS FOR FEATURES
    Boolean cameraInUse = false;
    Boolean gpsLocationInUse = false;
    ImageButton camera;
    Button gpsLocation;
    ArrayList<Unique> uniqueButtonsReferences = new ArrayList<Unique>();
    private final int MenuItem_EditId = 1, MenuItem_DeleteId = 0;
    SharedPreferences sharedPref;
    SharedPreferences.Editor prefEditor;

    /**
     * Runs on startup, creates the layout when the activity is created.
     * This is essentially the "main" method.
     *
     * @param savedInstanceState restores previous state on entry
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        prefEditor = sharedPref.edit();

        //initialize globals
        values = new ContentValues();
        httpFunc = new HTTPFunc(this);

        //setup layouts
        a_view = (LinearLayout) Layout.createActionBar(this, getSupportActionBar());
        layout = (LinearLayout) Layout.createMainLayout(this);
        ScrollView scroll = Layout.makeScroll(this,layout);
        mainFrame = new LinearLayout(this);
        mainFrame.setOrientation(LinearLayout.VERTICAL);
        setContentView(mainFrame);
        logoLayout = new RelativeLayout(this);
        ImageView logoView = new ImageView(this);
        try {
            InputStream logoAsset = this.getAssets().open("logo.png");
            Bitmap logo = BitmapFactory.decodeStream(logoAsset);
            logoView.setImageBitmap(logo);
            logoLayout.addView(logoView);
            logoView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.MATCH_PARENT));
            RelativeLayout.LayoutParams logoParams = (RelativeLayout.LayoutParams)logoView.getLayoutParams();
            logoParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            logoView.setLayoutParams(logoParams);
            a_view.addView(logoLayout);
            logoLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1f));
        }
        catch(IOException e){}

        defaultLayoutParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        defaultLayoutParams.setMargins(0, 10, 0, 10);

        scrollParameters = new
                LinearLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                scrollParameters.setMargins(0, 10, 0, 10);
                scrollParameters.weight = 1; //if not added the scroll will push the save button off screen

        mainFrame.addView(scroll, scrollParameters);

        //Setup environment
        dbHelper = new DatabaseHelper(this);
        Run.checkDate(this, sharedPref);

        //buildSubmit();
        dispatch();
        buildSave();
    }

    @Override
    public void onStart() {
        super.onStart();
        ConnectivityManager connectivityManager = (ConnectivityManager)
            this.getSystemService(Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        UpdateReceiver.netConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
        Log.i("NET", "Network Connected: " + UpdateReceiver.netConnected);
        Run.checkDate(this, sharedPref);
    }

    @Override
    public void onStop() {
        super.onStop();

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        db.close();
        wakelock.release();
    }

    /**
     * Receives all intents returned by activities when returning to this activity.
     * Right now, this only processes the intent returned by the getImage() method
     * below
     *
     * @param requestCode integer that identifies the type of activity that the intent belongs to
     * @param resultCode  status code returned by the exiting activity
     * @param data        the intent that is being returned by the exiting activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Img saved successfully", Toast.LENGTH_LONG).show();
            imgUri = data.getData();
        }
    }

    /**
     * Creates a file in the application's directory on the device, assigns a timestamped file
     * name, creates a new Image Capture intent, and starts it. the overridden method
     * "onActivityResult" handles the data returned by the camera intent.
     *
     * @return void
     */
    private void getImage() {
        File f = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File s = new File(f.getPath() + File.separator + timeStamp + ".jpg");
        Uri uri = Uri.fromFile(s);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void dispatch() {
        String Type;
        String Name;
        Scanner infile = null;
        db = dbHelper.getWritableDatabase();

        try {
            infile = new Scanner(this.getAssets().open("buildApp.txt"));   // scans File
        } catch (Exception e) {
            e.printStackTrace();
        }
        ReadFromInput readFile = new ReadFromInput(infile);

        do {
            try {
                readFile.getNextLine();
                readFile.ReadLineCollectInfo();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Type = readFile.getType();

            switch (Type) {
                case "id":
                    showIdEntry(readFile.getArgs(),this);
                    break;
                case "camera":
                    if (readFile.getAnswer() == 1) {
                        buildCamera(readFile.getArgs());
                        System.out.println(Type + " " + readFile.getAnswer());
                    }
                    break;
                case "gpsLoc":
                    if (readFile.getAnswer() == 1) {
                        buildGpsLoc(readFile.getArgs());
                        System.out.println(Type + " " + readFile.getAnswer());
                    }
                    break;

                case "gpsTracker":
                    if (readFile.getAnswer() == 1) {
                        buildGpsTracker(readFile.getArgs());
                        System.out.println(Type + " " + readFile.getAnswer());
                    }
                    break;

                case "unique":
                    Name = readFile.getUniqueName();
                    Unique u = new Unique(this,readFile.getArgs());
                    layout.addView(u.getView());
                    dbHelper.addColumn(db, Name, "TEXT");
                    uniqueButtonsReferences.add(u);
                    break;
                case "datetime":
                    date = new DateHelper(readFile.getArgs()[1]);
                    dbHelper.addColumn(db,readFile.getArgs()[1],"TEXT");
                    break;
                case "run":
                    dbHelper.addColumn(db, readFile.getArgs()[2], "TEXT");
                    sendRun = true;
                    break;
                default:
                    break;
            }
        }
        while (!Type.equals("endFile"));
        Cursor dbCursor = db.query("tasksTable", null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        for(String s : columnNames){
            System.out.println(s);
        }
    }

    public void showIdEntry(final String[] args, final Context c) {
        idTxt = new EditText(this);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Fuzion");
        adb.setMessage("Enter " + args[1]);
        adb.setView(idTxt);
        dbHelper.addColumn(db,args[1],"TEXT");
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(idTxt.getText().toString().equals("")){
                    showIdEntry(args,c);
                }
                else {
                    id_key = args[1];
                    id_value = idTxt.getText().toString();
                    System.out.printf("id_key=%s, id_value=%s\n", id_key, id_value);
                    SUBMIT_URL += "?idk=" + id_key + "&idv=" + id_value;
                    System.out.println(SUBMIT_URL);
                }
            }
        });
        adb.setCancelable(false);
        System.out.printf("key=%s, value=%s",id_key,id_value);
        adb.show();
    }
    public void buildCamera(String[] args) {
        // build button
        // add column to SQLite table
        String name = args[2];
        final ImageButton cameraButton = new ImageButton(this);
        dbHelper.addColumn(db, name, "TEXT");
        camera = cameraButton;
        cameraInUse = true;
        cameraButton.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.MATCH_PARENT));

        //cameraButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
              //LayoutParams.WRAP_CONTENT));
        cameraButton.setImageResource(android.R.drawable.ic_menu_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getImage();
            }
        });
        logoLayout.addView(cameraButton);
        RelativeLayout.LayoutParams cameraParams = (RelativeLayout.LayoutParams)cameraButton.getLayoutParams();
        cameraParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cameraButton.setLayoutParams(cameraParams);
    }

    public void buildGpsLoc(String[] args) {
        // build button
        // add column to SQLite table

        final Button buildGPSLocButton = new Button(this);

        gpsLocation = buildGPSLocButton;
        gpsLocationInUse = true;
        final String latColumn = args[2];
        final String longColumn = args[3];
        dbHelper.addColumn(db,latColumn,"TEXT");
        dbHelper.addColumn(db,longColumn,"TEXT");
        buildGPSLocButton.setText("GPS Location");
        buildGPSLocButton.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.MATCH_PARENT));
        buildGPSLocButton.setTextColor(Color.WHITE);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_FREQ, 0, locationListener);
        }
        catch(SecurityException e){

        }
        buildGPSLocButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buildGPSLocButton.setText("REDO GPS");
                values.put(latColumn,latitude);
                values.put(longColumn,longitude);
                Toast toast = Toast.makeText(getApplicationContext(),"Long:" + longitude + " Lat:" + latitude,Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        logoLayout.addView(buildGPSLocButton);
        RelativeLayout.LayoutParams gpsParams = (RelativeLayout.LayoutParams)buildGPSLocButton.getLayoutParams();
        gpsParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        buildGPSLocButton.setLayoutParams(gpsParams);
    }

    public void buildGpsTracker(String[] args) {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Fuzion");
        wakelock.acquire();
        if (args.length > 1 && args[2] != null) {
            GPS_FREQ = Integer.parseInt(args[2]);
        }
        if (args.length > 3) {
            dbHelper.addColumn(db, args[3], "TEXT");
            gps_tracker_lat = args[3];
            gps_tracker_long = args[4];
            dbHelper.addColumn(db, args[4], "TEXT");
        }
        sendGPS = true;
    }

    public void buildSubmit() {
        Button submitButton = new Button(this);
        submitButton.setText("Submit All Data");
        submitButton.setBackgroundColor(Color.BLACK);
        submitButton.setTextColor(Color.WHITE);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    upload();
                }
                catch(Exception e) {
                    Log.d("UPLOADER", "THAT DIDN'T WORK");
                }
            }
        });

        layout.addView(submitButton, defaultLayoutParams);
    }
    //GPS CODE
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            makeUseOfNewLocation(location);
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    private void makeUseOfNewLocation(Location l){
        latitude = l.getLatitude();
        longitude = l.getLongitude();
        gps_acc = l.getAccuracy();

        if(sendGPS && id_key != null && gps_acc <= 50f) {
            if(gps_tracker_lat != null && gps_tracker_long != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(gps_tracker_lat, latitude);
                contentValues.put(gps_tracker_long, longitude);
                date.insertDate(contentValues);
                contentValues.put(id_key,id_value);
                Run.checkDate(getApplication(), sharedPref);
                Run.insert(getApplication(), sharedPref, contentValues);
                db.insert("tasksTable",null,contentValues);
            }
            System.out.printf("Accuracy=%f,Longitude=%f,Latitude=%f\n",gps_acc,longitude, latitude); //Debug
        }
    }
    //END GPS CODE

    public void buildSave() {
        Button saveButton = new Button(this);
        saveButton.setText("Save");
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                values.put(id_key,id_value);

                Run.checkDate(getApplication(), sharedPref);          // Compares dates for persistent variables
                Run.insert(getApplication(), sharedPref, values);   // Inserts persistent into ContentValue object
                Run.increment(sharedPref);                          // Increments persistent variable

                for(Unique u : uniqueButtonsReferences){
                    String uText = u.getText();
                    if(!uText.isEmpty()) {
                        values.put(u.getName(), u.getText());
                    }
                }
                date.insertDate(values);
                try {
                    db.insert("tasksTable", null, values);
                }
                catch (SQLiteException e) {
                    Log.d("Database", "ERROR inserting: " + e.toString());
                }
                resetButtonsAfterSave();
                if(UpdateReceiver.netConnected) {
                    try {
                        upload();
                    }
                    catch(Exception e) {
                        Log.d("UPLOADER", "THAT DIDN'T WORK");
                    }
                }
            }
        });

        mainFrame.addView(saveButton, defaultLayoutParams);
    }

    public void resetButtonsAfterSave()
    {
        values = new ContentValues();

        if (cameraInUse == true)
        {
            camera.setImageResource(android.R.drawable.ic_menu_camera);
        }

        if (gpsLocationInUse == true)
        {
            gpsLocation.setText("GPS LOCATION");
        }

        for(Unique u : uniqueButtonsReferences) {
            u.clearText();
        }

    }
    public static void upload() throws JSONException {
        JSONArray j = new JSONArray();
        JSONObject jsonObject;
        Cursor c = dbHelper.queueAll(db);
        if(c.getCount() == 0)   //Does not submit if database is empty
            return;
        c.moveToNext();
        String[] cNames = c.getColumnNames();

        System.out.println();
        int cCount = c.getColumnCount();
        while(!c.isAfterLast()){
            jsonObject = new JSONObject();
            for(int i = 0; i < cCount; i++) {
                if(!cNames[i].equals("ID"))
                    jsonObject.put(cNames[i],c.getString(i));
            }
            j.put(jsonObject);
            //TODO NOT A SAFE WAY TO DELETE, LOOK TO REVISE, MD5?
            //OR DELETE AT END
            db.delete("tasksTable","ID=" + c.getString(0),null);
            c.moveToNext();
        }
        JSONObject params = new JSONObject();
        params.put("data",j);
        httpFunc.doHTTPpost(SUBMIT_URL,j,imgUri);
        c.close();
    }

}
