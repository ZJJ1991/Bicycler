package com.example.jzhou.bicycler;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aware.Accelerometer;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Locations;
import com.aware.providers.Accelerometer_Provider;
import com.aware.providers.Locations_Provider;
import com.aware.providers.Magnetometer_Provider;

import java.sql.SQLException;
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
    public static char[] Device_id;
    public static RadioGroup gpsradioGroup;
    public static RadioGroup accradioGroup;
    public static Button start_btn;
    public static Button stop_btn;



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


        long epoch = System.currentTimeMillis();
        String timestamp = String.valueOf(epoch);
        double Timestamp = Double.parseDouble(timestamp);
        longitude = 11.12;
        latitude = 23.44;
        altitude = 123.33;
        speed = 123.23;
        String sql = "insert into bicyclers.\"Location\"(timestamp, longitude, latitude, altitude, speed, geom)values("+Timestamp+","+longitude+","+latitude+","+altitude+","+speed+"st_point("+longitude +","+latitude+")" +")";
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



        String device_id = Aware_Preferences.DEVICE_ID;
        Device_id = device_id.toCharArray();
        Log.d("device_id", Aware_Preferences.DEVICE_ID);



        //initialise Aware
        Intent aware = new Intent(this, Aware.class);
        startService(aware);

        //Activate sensor




        Log.d(DEBUG, "1");

        start_btn = (Button) findViewById(R.id.start_btn);
        stop_btn = (Button) findViewById(R.id.stop_btn);

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Aware.setSetting(Plugin.this, Aware_Preferences.STATUS_LOCATION_GPS, true);
                Log.d(DEBUG, "3");
                Aware.setSetting(Plugin.this, Aware_Preferences.FREQUENCY_LOCATION_GPS, 10);


                Aware.setSetting(Plugin.this, Aware_Preferences.STATUS_ACCELEROMETER, true);
                Aware.setSetting(Plugin.this, Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);

                Aware.startSensor(Plugin.this, Aware_Preferences.STATUS_ACCELEROMETER);
                Aware.startSensor(Plugin.this, Aware_Preferences.STATUS_LOCATION_GPS);
            }
        });

        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
            }
        });

        gpsradioGroup = (RadioGroup) findViewById(R.id.Gps_fre_radio);
        gpsradioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonID = group.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(radioButtonID);
                if (rb.getText().equals("180 seconds")) {
                    Log.d(DEBUG, "set gps frequency 180");
                    Aware.setSetting(Plugin.this, Aware_Preferences.FREQUENCY_LOCATION_GPS, 180);
                    Log.d(DEBUG, "finished gps frequency 180");
                    Toast toast = Toast.makeText(Plugin.this, "GPS frequency has been changed", Toast.LENGTH_SHORT);
                    toast.show();
                }
                if (rb.getText().equals("30 seconds")) {
                    Log.d(DEBUG, "set gps frequency 30");
                    Aware.setSetting(Plugin.this, Aware_Preferences.FREQUENCY_LOCATION_GPS, 30);
                    Log.d(DEBUG, "finished gps frequency 30");
                    Toast toast = Toast.makeText(Plugin.this, "GPS frequency has been changed", Toast.LENGTH_SHORT);
                    toast.show();
                }
                if (rb.getText().equals("10 seconds")) {
                    Log.d(DEBUG, "set gps frequency 10");
                    Aware.setSetting(Plugin.this, Aware_Preferences.FREQUENCY_LOCATION_GPS, 10);
                    Log.d(DEBUG, "finished gps frequency 10");
                    Toast toast = Toast.makeText(Plugin.this, "GPS frequency has been changed", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        accradioGroup = (RadioGroup) findViewById(R.id.Acc_fre_radio);
        accradioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonID = group.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(radioButtonID);
                if (rb.getText().equals("200000 (normal)")) {
                    Log.d(DEBUG, "frequency 200000");
                    Aware.setSetting(Plugin.this, Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);
                    Log.d(DEBUG, "finished frequency 200000");
                    Toast toast = Toast.makeText(Plugin.this, "Accelerometer frequency has been changed", Toast.LENGTH_SHORT);
                    toast.show();
                }
                if (rb.getText().equals("60000 (UI)")){
                    Log.d(DEBUG,"frequency 60000");
                    Aware.setSetting(Plugin.this, Aware_Preferences.FREQUENCY_ACCELEROMETER, 60000);
                    Log.d(DEBUG, "finished frequency 60000");
                    Toast toast = Toast.makeText(Plugin.this, "Accelerometer frequency has been changed", Toast.LENGTH_SHORT);
                    toast.show();
                }
                if (rb.getText().equals("20000 (game)")){
                    Log.d(DEBUG,"frequency 20000");
                    Aware.setSetting(Plugin.this, Aware_Preferences.FREQUENCY_LOCATION_GPS, 20000);
                    Log.d(DEBUG, "finished frequency 20000");
                    Toast toast = Toast.makeText(Plugin.this, "Accelerometer frequency has been changed", Toast.LENGTH_SHORT);
                    toast.show();
                }
                if (rb.getText().equals("0 (fastest)")){
                    Log.d(DEBUG,"frequency 0");
                    Aware.setSetting(Plugin.this, Aware_Preferences.FREQUENCY_ACCELEROMETER, 0);
                    Log.d(DEBUG, "finished frequency 0");
                    Toast toast = Toast.makeText(Plugin.this, "Accelerometer frequency has been changed", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        IntentFilter filter = new IntentFilter();
        Log.d(DEBUG, "4");

        filter.addAction(Locations.ACTION_AWARE_LOCATIONS);
        filter.addAction(Accelerometer.ACTION_AWARE_ACCELEROMETER);
        registerReceiver(Datareceiver, filter);
        Log.d(DEBUG, "5");


    }



    public static String getDeviceId(Context context){
        final TelephonyManager tm = (TelephonyManager) context
            .getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = tm.getDeviceId();
        tmSerial = tm.getSimSerialNumber();
        androidId = android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        Log.d("debug", "uuid=" + uniqueId);
        return uniqueId;
    }


    datareceiver Datareceiver = new datareceiver();
    private class datareceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Locations.ACTION_AWARE_LOCATIONS)){
                Log.d(DEBUG,"try getting location data ");
                Cursor location = context.getContentResolver().query
                        (Locations_Provider.Locations_Data.CONTENT_URI, null, null, null, Locations_Provider.Locations_Data.TIMESTAMP + " DESC LIMIT 1");
                Log.d(DEBUG,"7");
                if(location!=null&&location.moveToFirst()){
                    Log.d(DEBUG,"8");
                   speed = location.getDouble(location.getColumnIndex(Locations_Provider.Locations_Data.SPEED));
                    Log.d(DEBUG,"9");
                    longitude = location.getDouble(location.getColumnIndex(Locations_Provider.Locations_Data.LONGITUDE));
                    latitude = location.getDouble(location.getColumnIndex(Locations_Provider.Locations_Data.LATITUDE));
                    altitude = location.getDouble(location.getColumnIndex(Locations_Provider.Locations_Data.ALTITUDE));
                    long epoch = System.currentTimeMillis();
                    String timestamp = String.valueOf(epoch);
                    double Timestamp = Double.parseDouble(timestamp);
                    String sql = "insert into bicyclers.\"Location\"(timestamp, longitude, latitude, altitude, speed, geom)values("+Timestamp+","+longitude+","+latitude+","+altitude+","+speed+"st_point("+longitude +","+latitude+")" +")";
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


                    Log.d(DEBUG, "final");


                    Log.d(DEBUG, "10");
                }
            }

            if (intent.getAction().equals(Accelerometer.ACTION_AWARE_ACCELEROMETER)){
                Log.d(DEBUG, "try to get accelerometer data");

                ContentValues data = intent.getParcelableExtra(Accelerometer.EXTRA_DATA);

                accelerometer_x = data.getAsDouble(Accelerometer_Provider.Accelerometer_Data.VALUES_0);
                accelerometer_y = data.getAsDouble(Accelerometer_Provider.Accelerometer_Data.VALUES_1);
                accelerometer_z = data.getAsDouble(Accelerometer_Provider.Accelerometer_Data.VALUES_2);
                Log.d(DEBUG, data.toString());
                long epoch = System.currentTimeMillis();
                String timestamp = String.valueOf(epoch);
                double Timestamp = Double.parseDouble(timestamp);
                String sql = "insert into bicyclers.\"Accelerometer\"(timestamp, value_x, value_y, value_z, value)values(" + Timestamp + "," + accelerometer_x + "," + accelerometer_y + "," + accelerometer_z + "," + acc +")";
                try {
                    lo = new LongOperation(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                lo.execute();
                Log.d(DEBUG, "data sql acce 2");

//                Cursor accelerometer  = context.getContentResolver().query
//                        (Accelerometer_Provider.Accelerometer_Data.CONTENT_URI, null, null, null, Accelerometer_Provider.Accelerometer_Data.TIMESTAMP + " DESC LIMIT 1");
//                Log.d(DEBUG, "cursor data");
//                Log.d("test", accelerometer.moveToFirst()+"sa");
//
//                if (accelerometer!=null&&accelerometer.moveToFirst()) {
//                    accelerometer_x = accelerometer.getDouble(accelerometer.getColumnIndex(Accelerometer_Provider.Accelerometer_Data.VALUES_0));
//                    accelerometer_y = accelerometer.getDouble(accelerometer.getColumnIndex(Accelerometer_Provider.Accelerometer_Data.VALUES_1));
//                    accelerometer_z = accelerometer.getDouble(accelerometer.getColumnIndex(Accelerometer_Provider.Accelerometer_Data.VALUES_2));
//                    Log.d(DEBUG, "value of acc");
//                    if (accelerometer != null && !accelerometer.isClosed()){accelerometer.close();}
//                    accelerometer.close();
//                    Log.d(DEBUG,"close cursor");
//                    acc = Math.sqrt(accelerometer_x * accelerometer_x + accelerometer_y * accelerometer_y + accelerometer_z * accelerometer_z);
//                    acc_txt.setText(acc + " ");
//                    long epoch = System.currentTimeMillis();
//                    String timestamp = String.valueOf(epoch);
//                    double Timestamp = Double.parseDouble(timestamp);
//
//                    String sql = "insert into bicyclers.\"Accelerometer\"(timestamp, value_x, value_y, value_z, value, Device_id)values(" + Timestamp + "," + accelerometer_x + "," + accelerometer_y + "," + accelerometer_z + "," + acc + "sada" + ")";
//                    Log.d(DEBUG, "data sql acce 1");
//                    Log.d("Device-id", Aware_Preferences.DEVICE_ID);
//                    lo = new LongOperation(sql);
//                    lo.execute();
//                    Log.d(DEBUG, "data sql acce 2");
//                 }

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

        unregisterReceiver(Datareceiver);
        Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER, false);
        Aware.setSetting(this, Aware_Preferences.STATUS_LOCATION_GPS, false);
        Aware.stopPlugin(this,"com.example.jzhou.bicybler");
    }


        private class LongOperation extends AsyncTask{
            public  String sql;
           LongOperation(String s) throws SQLException, ClassNotFoundException {
                sql = s;
               Posconn = new PostgreSqlCon();


            }
        @Override
        protected Object doInBackground(Object[] params) {
            Log.d(DEBUG,"execute sql");
            try {
                Posconn.connection(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            Log.d(DEBUG,"end sql");
            return null;
        }
    }

}
