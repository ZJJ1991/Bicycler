package com.example.jzhou.bicycler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aware.Aware;
import com.aware.Aware_Preferences;

import org.w3c.dom.Text;

import static android.view.View.inflate;

/**
 * Created by jzhou on 03/12/2015.
 */
public class GpsFrequency extends DialogFragment {
    public static TextView gpsfre_txtv;






    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("GPS FREQUENCY SETTING");
        builder.setMessage("Type the frequency");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View myview = inflater.inflate(R.layout.gpsfre, null);
        builder.setView(myview)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                    gpsfre = Integer.parseInt(gps_fre.getText().toString());
                        gpsfre_txtv = (TextView) myview.findViewById(R.id.gpsfre_txt);
                        String tvValue = gpsfre_txtv.getText().toString();
                        int num1 = 10;
                        // &&!tvValue.equals(......)
                        if (!tvValue.equals("")  ) {
                           num1  = Integer.parseInt(tvValue);
                            Log.d("Kanni", num1 + "");

                            if (num1>30){
                                Toast.makeText(GpsFrequency.this.getActivity(), "The range of GPS frequency is [0,30] seconds. Please reset the frequency!", Toast.LENGTH_LONG).show();}
                            else{
                                Log.d("ELSECHECKING", "to see else");
                                Aware.setSetting(GpsFrequency.this.getActivity(), Aware_Preferences.FREQUENCY_LOCATION_GPS, num1);}

                        }

            }
        })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Toast.makeText(GpsFrequency.this.getActivity(), "The frequency hasn't changed", Toast.LENGTH_LONG).show();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }


}
