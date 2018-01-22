package com.example.siubo.wheretoyov50;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Settings extends AppCompatActivity {
    protected int PLACE_PICKER_REQUEST = 1;
    protected int iamgender;
    protected String FILENAME = "user_info";
    protected String HOME_FILENAME = "home_info";
    protected double home_lat;
    protected double home_lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent = getIntent();
        Log.d("MAIN", "Settings onCreate");
        iamgender = intent.getExtras().getInt("IAMGENDER");
        home_lat = intent.getExtras().getDouble("HOME_LAT");
        home_lng = intent.getExtras().getDouble("HOME_LNG");
        if (iamgender == 0)
            ((CheckBox) findViewById(R.id.imfemale_checkBox)).setChecked(true);
        if (iamgender == 1)
            ((CheckBox) findViewById(R.id.immale_checkBox)).setChecked(true);
    }

    public void onGenderCheckboxClicked(View view){
        if (view.getId() == R.id.immale_checkBox){
            ((CheckBox) view).setChecked(true);
            ((CheckBox) findViewById(R.id.imfemale_checkBox)).setChecked(false);
            iamgender = 1;
        }
        else if (view.getId() == R.id.imfemale_checkBox) {
            ((CheckBox) view).setChecked(true);
            ((CheckBox) findViewById(R.id.immale_checkBox)).setChecked(false);
            iamgender = 0;
        }
    }

    public void ChooseHomeClick(View view) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(this, data);
                home_lat = place.getLatLng().latitude;
                home_lng = place.getLatLng().longitude;
            }
        }
    }

    public void SaveClick(View view){
        FileOutputStream fos = null;
        String newline = "\n";
        try {
            fos = openFileOutput(HOME_FILENAME, Context.MODE_PRIVATE);
            fos.write(Double.toString(home_lat).getBytes());
            fos.write(newline.getBytes());
            fos.write(Double.toString(home_lng).getBytes());
            fos.write(newline.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(Integer.toString(iamgender).getBytes());
            fos.write(newline.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("MAIN", "Info Saved");
        Log.d("MAIN", "Home Lat: " + Double.toString(home_lat));
        Log.d("MAIN", "Home Lng: " + Double.toString(home_lng));
        Log.d("MAIN", "iamgender: " + Integer.toString(iamgender));
        Toast.makeText(this, "We will not disclose your personal information.", Toast.LENGTH_SHORT).show();
    }
}

