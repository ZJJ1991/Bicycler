package com.example.jzhou.bicycler;

import android.content.BroadcastReceiver;
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
import android.widget.TextView;
import android.widget.Toast;

import com.aware.Accelerometer;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Locations;
import com.aware.providers.Accelerometer_Provider;
import com.aware.providers.Locations_Provider;
import com.aware.providers.Magnetometer_Provider;

import java.util.UUID;

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
    public TextView txtview;
    public TextView acc_txt;
    public PostgreSqlCon Posconn;
    public static LongOperation lo;
    public static char[] Device_id;


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

        txtview = (TextView) findViewById(R.id.speed);
        acc_txt = (TextView) findViewById(R.id.accelerometer);
        String device_id = Aware_Preferences.DEVICE_ID;
        Device_id = device_id.toCharArray();
        Log.d("device_id", Aware_Preferences.DEVICE_ID);
        Intent aware = new Intent(this, Aware.class);
        startService(aware);

        Log.d(DEBUG, "1");

        Aware.setSetting(this, Aware_Preferences.STATUS_LOCATION_GPS, true);
        Log.d(DEBUG, "3");
        Aware.setSetting(this, Aware_Preferences.FREQUENCY_LOCATION_GPS, 10);

        Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER, true);
        Aware.setSetting(this, Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);



        IntentFilter filter = new IntentFilter();
        Log.d(DEBUG, "4");

        filter.addAction(Locations.ACTION_AWARE_LOCATIONS);
        filter.addAction(Accelerometer.ACTION_AWARE_ACCELEROMETER);

        Intent intent = new Intent(Locations.ACTION_AWARE_LOCATIONS);
        sendBroadcast(intent);

        Intent acc_intent= new Intent(Accelerometer.ACTION_AWARE_ACCELEROMETER);
        sendBroadcast(acc_intent);

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
                Log.d(DEBUG,"location data ");
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
                    txtview.setText(longitude + " ");
                    long epoch = System.currentTimeMillis();
                    String timestamp = String.valueOf(epoch);
                    double Timestamp = Double.parseDouble(timestamp);
                    String sql = "insert into bicyclers.\"Location\"(timestamp, longitude, latitude, altitude, speed)values("+Timestamp+","+longitude+","+latitude+","+altitude+","+speed+")";
                    Log.d(DEBUG, "data sql location 1");
                    lo = new LongOperation(sql);
                    lo.execute();
                    Log.d(DEBUG, "data sql location 2");


                    Log.d(DEBUG, "final");


                    Log.d(DEBUG, "10");
                }
            }

            if (intent.getAction().equals(Accelerometer.ACTION_AWARE_ACCELEROMETER)){
                Log.d(DEBUG,"try to get accelerometer data");
                Cursor accelerometer  = context.getContentResolver().query
                        (Accelerometer_Provider.Accelerometer_Data.CONTENT_URI, null, null, null, Accelerometer_Provider.Accelerometer_Data.TIMESTAMP + " DESC LIMIT 1");
                Log.d(DEBUG, "data sql 1");

                Log.d(DEBUG, "data sql 2");
                if (accelerometer!=null&&accelerometer.moveToFirst()){
                    accelerometer_x = accelerometer.getDouble(accelerometer.getColumnIndex(Accelerometer_Provider.Accelerometer_Data.VALUES_0));
                    accelerometer_y = accelerometer.getDouble(accelerometer.getColumnIndex(Accelerometer_Provider.Accelerometer_Data.VALUES_1));
                    accelerometer_z = accelerometer.getDouble(accelerometer.getColumnIndex(Accelerometer_Provider.Accelerometer_Data.VALUES_2));
                    Log.d(DEBUG, "value of acc");
                    acc = Math.sqrt(accelerometer_x * accelerometer_x + accelerometer_y * accelerometer_y + accelerometer_z * accelerometer_z);
                    acc_txt.setText(acc+" ");
                    String device_id = "sadas-1wa";
                    long epoch = System.currentTimeMillis();
                    String timestamp = String.valueOf(epoch);
                    double Timestamp = Double.parseDouble(timestamp);

                    String sql = "insert into bicyclers.\"Accelerometer\"(timestamp, value_x, value_y, value_z, value)values("+Timestamp+","+accelerometer_x+","+accelerometer_y+","+accelerometer_z+","+acc+")";
                    Log.d(DEBUG, "data sql acce 1");
                    Log.d(DEBUG, "data sql acce 2");
                    Log.d("Device-id", Aware_Preferences.DEVICE_ID);
                    lo = new LongOperation(sql);
                    lo.execute();
                }
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
        Aware.setSetting(this, Aware_Preferences.STATUS_LOCATION_GPS, false);
        Aware.stopPlugin(this,"com.example.jzhou.bicybler");
    }


        private class LongOperation extends AsyncTask{
            public  String sql;
           LongOperation(String s){
                sql = s;
            }
        @Override
        protected Object doInBackground(Object[] params) {
            Log.d(DEBUG,"execute sql");
            String abc = "select * from bicyclers.\"Accelerometer\"";

            Posconn = new PostgreSqlCon(sql);
            Log.d(DEBUG,"end sql");


            return null;
        }
    }

}
