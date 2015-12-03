package com.example.jzhou.bicycler;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aware.Accelerometer;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Locations;
import com.aware.providers.Accelerometer_Provider;
import com.aware.providers.Locations_Provider;
import com.aware.providers.Magnetometer_Provider;
import com.aware.utils.Aware_Sensor;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

import static android.widget.Toast.LENGTH_LONG;

public class Plugin extends AppCompatActivity {

    private static String DEBUG = "DEBUGGING";
    public static double speed;
    public static double latitude;
    public static double longitude;
    public static double altitude;
    public static double accelerometer_x;
    public static double accelerometer_y;
    public static double accelerometer_z;
    public static double acc;

    public PostgreSqlCon Posconn;
    public static LongOperation lo;
    public static String Device_id;

    public static Button start_btn;
    public static Button stop_btn;

    public static CheckBox gps_checkBox;
    public static CheckBox acc_checkBox;

    public static Button gpsfre_btn;
    public static Button accfre_btn;
    public static IntentFilter filter;
    public static boolean acc_flag = false;
    public static boolean gps_flag = false;


    TextView grefre_txt;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Device_id = getDeviceId(this);


        //initialise Aware
        Intent aware = new Intent(this, Aware.class);
        startService(aware);

        //Activate sensor


        Log.d(DEBUG, "1");

        long epoch = System.currentTimeMillis();
        Timestamp Timestamp = new Timestamp(epoch);
        String sql = "insert into bicyclers.\"Users\"(deviceid, timestamp)values(" + "'" + Device_id + "'" + "," + "'" + Timestamp + "'" + ")";
        try {
            lo = new LongOperation(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        lo.execute();

        Aware.setSetting(Plugin.this, Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);
        Aware.setSetting(Plugin.this, Aware_Preferences.FREQUENCY_LOCATION_GPS, 10);

        grefre_txt = (TextView) findViewById(R.id.gpsfre_txt);
        //grefre_txt.setText("" + 4);
        gpsfre_btn = (Button) findViewById(R.id.gpsfre_btn);
        gpsfre_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GpsFrequency gpsFre = new GpsFrequency();
                gpsFre.show(getFragmentManager(), "gps frequency alert");
            }
        });








        accfre_btn = (Button) findViewById(R.id.accfre_btn);
        accfre_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccFrequency accFre = new AccFrequency();
                accFre.show(getFragmentManager(), "accelerometer sampling rate alert");
            }
        });


        filter = new IntentFilter();
        Log.d(DEBUG, "4");

        filter.addAction(Locations.ACTION_AWARE_LOCATIONS);
        filter.addAction(Accelerometer.ACTION_AWARE_ACCELEROMETER);


        gps_checkBox = (CheckBox) findViewById(R.id.gps_checkbox);


        gps_checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gps_checkBox.isChecked()) {
                    Log.d(DEBUG, "close gps");
                    if (gps_flag) {
                        unregisterReceiver(gpsreceiver);
                        gps_flag = false;
                        Log.d(DEBUG, "unregister gps");
                    }
//                    Aware.stopSensor(Plugin.this, Aware_Preferences.STATUS_LOCATION_GPS);
                    Aware.setSetting(Plugin.this, Aware_Preferences.STATUS_LOCATION_GPS, false);
                    Log.d(DEBUG, "stop gps");
                }
                if (gps_checkBox.isChecked()) {
                    Log.d(DEBUG, "open gps");
                    Aware.setSetting(Plugin.this, Aware_Preferences.STATUS_LOCATION_GPS, true);
                    Aware.startSensor(Plugin.this, Aware_Preferences.STATUS_LOCATION_GPS);
                    Log.d(DEBUG, "gps is opened");

                }
            }
        });


        acc_checkBox = (CheckBox) findViewById(R.id.acc_checkbox);
        acc_checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!acc_checkBox.isChecked()) {
                    Log.d(DEBUG, "close accelerometer");
                    if (acc_flag) {
                        unregisterReceiver(accelerometerreceiver);
                        acc_flag = false;
                        Log.d(DEBUG, "unregister acc");
                    }
//                    Aware.stopSensor(Plugin.this, Aware_Preferences.STATUS_ACCELEROMETER);
                    Aware.setSetting(Plugin.this, Aware_Preferences.STATUS_ACCELEROMETER, false);
                    Log.d(DEBUG, "stop accelerometer");
                }
                if (acc_checkBox.isChecked()) {
                    Log.d(DEBUG, "open accelerometer");
                    Aware.setSetting(Plugin.this, Aware_Preferences.STATUS_ACCELEROMETER, true);
                    Aware.startSensor(Plugin.this, Aware_Preferences.STATUS_ACCELEROMETER);
                    Log.d(DEBUG, "accelerometer is opened");
                }

            }
        });


        start_btn = (Button) findViewById(R.id.start_btn);
        stop_btn = (Button) findViewById(R.id.stop_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(DEBUG, "start sensors");
                if (acc_checkBox.isChecked()) {

                    registerReceiver(accelerometerreceiver, filter);
                    Log.d(DEBUG, "acc has started");
                }
                if (gps_checkBox.isChecked()) {

                    registerReceiver(gpsreceiver, filter);
                    Log.d(DEBUG, "5");
                    Log.d(DEBUG, "gps has started");
                }
                stop_btn.setClickable(true);
                stop_btn.setBackgroundColor(Color.BLUE);
                Log.d(DEBUG, "color 10");
                //start_btn.setClickable(false);
            }
        });


        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //unregisterReceiver(Datareceiver);
                Posconn.close();
                Log.d(DEBUG, "stop 1");
                if (gps_flag) {
                    Log.d(DEBUG, "stop haha");
                    unregisterReceiver(gpsreceiver);
                    gps_flag = false;
                }
                if (acc_flag) {
                    Log.d(DEBUG, "stop hehe");
                    unregisterReceiver(accelerometerreceiver);
                    acc_flag = false;
                }
                Log.d(DEBUG, "stop 2");

//                Aware.stopSensor(Plugin.this, Aware_Preferences.STATUS_ACCELEROMETER);
//                Log.d(DEBUG, "stop 3");
                // Aware.stopSensor(Plugin.this, Aware_Preferences.STATUS_LOCATION_GPS);
                //  Log.d(DEBUG, "stop 4");
//                Aware.setSetting(Plugin.this, Aware_Preferences.STATUS_ACCELEROMETER, false);
//                Aware.setSetting(Plugin.this, Aware_Preferences.STATUS_LOCATION_GPS, false);
                Log.d(DEBUG, "close sensors");
                //start_btn.setClickable(true);
                stop_btn.setBackgroundColor(Color.BLACK);
                stop_btn.setClickable(false);
                Log.d(DEBUG, "color 5");
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public static String getDeviceId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = tm.getDeviceId();
        tmSerial = tm.getSimSerialNumber();
        androidId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        Log.d("debug", "uuid=" + uniqueId);
        return uniqueId;
    }


    datareceiver gpsreceiver = new datareceiver();

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Plugin Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.jzhou.bicycler/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Plugin Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.jzhou.bicycler/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private class datareceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Locations.ACTION_AWARE_LOCATIONS)) {
                gps_flag = true;
                Log.d(DEBUG, "try getting location data ");
                Cursor location = context.getContentResolver().query
                        (Locations_Provider.Locations_Data.CONTENT_URI, null, null, null, Locations_Provider.Locations_Data.TIMESTAMP + " DESC LIMIT 1");
                Log.d(DEBUG, "7");
                if (location != null && location.moveToFirst()) {
                    Log.d(DEBUG, "8");
                    speed = location.getDouble(location.getColumnIndex(Locations_Provider.Locations_Data.SPEED));
                    Log.d(DEBUG, "9");
                    longitude = location.getDouble(location.getColumnIndex(Locations_Provider.Locations_Data.LONGITUDE));
                    latitude = location.getDouble(location.getColumnIndex(Locations_Provider.Locations_Data.LATITUDE));
                    altitude = location.getDouble(location.getColumnIndex(Locations_Provider.Locations_Data.ALTITUDE));
                    long epoch = System.currentTimeMillis();
                    String timestamp = String.valueOf(epoch);
                    Timestamp Timestamp = new Timestamp(epoch);
                    Log.d("TIMESTAMP11", Timestamp + "");
                    String sql = "insert into bicyclers.\"Location\"(longitude, latitude, altitude, speed, geom, deviceid, timestamp)values(" + longitude + "," + latitude + "," + altitude + "," + speed + "," + "st_point(" + longitude + "," + latitude + ")" + "," + "'" + Device_id + "'" + "," + "'" + Timestamp + "'" + ")";
                    Log.d(DEBUG, "data sql location 1");
                    try {
                        lo = new LongOperation(sql);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    lo.execute();
                    Log.d(DEBUG, "data sql location 2");

                }
            }

            if (intent.getAction().equals(Locations.ACTION_AWARE_GPS_LOCATION_DISABLED)) {
                Aware.stopSensor(Plugin.this, Aware_Preferences.STATUS_LOCATION_GPS);
            }

        }
    }

    acc_datareceiver accelerometerreceiver = new acc_datareceiver();

    private class acc_datareceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Accelerometer.ACTION_AWARE_ACCELEROMETER)) {
                acc_flag = true;
                Log.d(DEBUG, "try to get accelerometer data");

                ContentValues data = intent.getParcelableExtra(Accelerometer.EXTRA_DATA);

                accelerometer_x = data.getAsDouble(Accelerometer_Provider.Accelerometer_Data.VALUES_0);
                accelerometer_y = data.getAsDouble(Accelerometer_Provider.Accelerometer_Data.VALUES_1);
                accelerometer_z = data.getAsDouble(Accelerometer_Provider.Accelerometer_Data.VALUES_2);
                acc = Math.sqrt(accelerometer_x * accelerometer_x + accelerometer_y * accelerometer_y + accelerometer_z * accelerometer_z);
                Log.d(DEBUG, data.toString());
                long epoch = System.currentTimeMillis();
                Timestamp Timestamp = new Timestamp(epoch);
                String sql = "insert into bicyclers.\"Accelerometer\"(value_x, value_y, value_z, value, deviceid, timestamp)values(" + accelerometer_x + "," + accelerometer_y + "," + accelerometer_z + "," + acc + "," + "'" + Device_id + "'" + "," + "'" + Timestamp + "'" + ")";
                try {
                    lo = new LongOperation(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                lo.execute();
                Log.d(DEBUG, "data sql acce 2");
            }


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Posconn.close();

        unregisterReceiver(gpsreceiver);
        unregisterReceiver(accelerometerreceiver);
        Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER, false);
        Aware.setSetting(this, Aware_Preferences.STATUS_LOCATION_GPS, false);
        Aware.stopPlugin(this, "com.example.jzhou.bicybler");
    }


    private class LongOperation extends AsyncTask {
        public String sql;

        LongOperation(String s) throws SQLException, ClassNotFoundException {
            sql = s;
            Posconn = new PostgreSqlCon();


        }

        @Override
        protected Object doInBackground(Object[] params) {
            Log.d(DEBUG, "execute sql");
            try {
                Posconn.connection(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            Log.d(DEBUG, "end sql");
            return null;
        }
    }

}
