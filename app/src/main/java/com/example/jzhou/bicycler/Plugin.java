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

        //registerReceiver(Datareceiver, filter);
        //Log.d(DEBUG, "5");

        LongOperation lo = new LongOperation();
        lo.execute();
    }




    datareceiver Datareceiver = new datareceiver();
    private class datareceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Locations.ACTION_AWARE_LOCATIONS)){
                Log.d(DEBUG,"6");
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

                    //txtview.setText(speed + " ");
                    Log.d(DEBUG, "final");
                   // Toast toast = Toast.makeText(context, speed+" ", Toast.LENGTH_SHORT);
                   // toast.show();
                    Log.d(DEBUG, "10");
                }
            }

            if (intent.getAction().equals(Accelerometer.ACTION_AWARE_ACCELEROMETER)){
                Log.d(DEBUG,"try to get accelerometer data");
                Cursor accelerometer  = context.getContentResolver().query
                        (Accelerometer_Provider.Accelerometer_Data.CONTENT_URI, null, null, null, Accelerometer_Provider.Accelerometer_Data.TIMESTAMP + " DESC LIMIT 1");
                //Log.d(DEBUG,"cursor data");
                //String sq = "select * from  Accelerometer";
                //Log.d(DEBUG, "data sql 1");
                //Posconn = new PostgreSqlCon(sq);
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
                    String sym = "Accelerometer" ;
                    String sql1 = "insert into bicyclers.";
                    String sql2 = "\"sym\"(timestamp,  value_x, value_y, value_z, value)";
                    String sql3 = "values(123111,  16612.21, 1881.232, 21412.221, 2132.163)";
                    String sql12 = "insert into bicyclers.\"Accelerometer\"(timestamp,  value_x, value_y, value_z, value)values(123111,  16612.21, 1881.232, 21412.221, 2132.163)";
                    Log.d(DEBUG, "data sql acce 1");
                    Posconn = new PostgreSqlCon(sql12);
                    Log.d(DEBUG, "data sql acce 2");
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

        @Override
        protected Object doInBackground(Object[] params) {
            Log.d(DEBUG,"execute sql");
            String abc = "select * from bicyclers.\"Accelerometer\"";
            String sql = "insert into bicyclers.\"Accelerometer\"(timestamp,  value_x, value_y, value_z, value)values(3223111,  26612.21, 6881.232, 33412.221, 6632.163)";

            Posconn = new PostgreSqlCon(sql);
            Log.d(DEBUG,"end sql");
//            IntentFilter filter = new IntentFilter();
//            Log.d(DEBUG, "4");
//
//            filter.addAction(Locations.ACTION_AWARE_LOCATIONS);
//            filter.addAction(Accelerometer.ACTION_AWARE_ACCELEROMETER);
//
//            Intent intent = new Intent(Locations.ACTION_AWARE_LOCATIONS);
//            sendBroadcast(intent);
//
//            Intent acc_intent= new Intent(Accelerometer.ACTION_AWARE_ACCELEROMETER);
//            sendBroadcast(acc_intent);
//            registerReceiver(Datareceiver, filter);

            return null;
        }
    }

}
