package com.example.jzhou.bicycler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aware.Aware;
import com.aware.Aware_Preferences;

/**
 * Created by jzhou on 03/12/2015.
 */
public class AccFrequency extends DialogFragment {
    TextView accfre_txtv;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ACCELEROMETER FREQUENCY SETTING");
        builder.setMessage("Type the frequency");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View myview = inflater.inflate(R.layout.accfre, null);
        builder.setView(myview)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                    gpsfre = Integer.parseInt(gps_fre.getText().toString());
                        accfre_txtv = (TextView) myview.findViewById(R.id.accfre_txt);
                        String tvValue = accfre_txtv.getText().toString();
                        int num1 = 10;
                        // &&!tvValue.equals(......)
                        if (!tvValue.equals("")  ) {
                            num1  = Integer.parseInt(tvValue);
                            Log.d("Kanni", num1 + "");

                            if (num1>200000){
                                Toast.makeText(AccFrequency.this.getActivity(), "The range of Accelerometer sampling rate is [0,200000] . Please reset the frequency!", Toast.LENGTH_LONG).show();}
                            else{
                                Log.d("ELSECHECKING", "to see else");
                                if (num1<=200000&&num1>=100000)
                                    Toast.makeText(AccFrequency.this.getActivity(), "Accelerometer frequency is slow", Toast.LENGTH_LONG).show();
                                if (num1<100000&&num1>=40000)
                                    Toast.makeText(AccFrequency.this.getActivity(), "Accelerometer frequency is normal", Toast.LENGTH_LONG).show();
                                if (num1<40000&&num1>=15000)
                                    Toast.makeText(AccFrequency.this.getActivity(), "Accelerometer frequency is fast", Toast.LENGTH_LONG).show();
                                if (num1<15000)
                                    Toast.makeText(AccFrequency.this.getActivity(), "Accelerometer frequency is extremely fast", Toast.LENGTH_LONG).show();
                                Aware.setSetting(AccFrequency.this.getActivity(), Aware_Preferences.FREQUENCY_LOCATION_GPS, num1);}
                        }

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Toast.makeText(AccFrequency.this.getActivity(), "The frequency hasn't changed", Toast.LENGTH_LONG).show();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
