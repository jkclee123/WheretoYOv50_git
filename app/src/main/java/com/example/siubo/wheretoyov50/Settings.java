package com.example.siubo.wheretoyov50;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Settings extends AppCompatActivity {
    protected int PLACE_PICKER_REQUEST = 1;
    protected int my_attri, is_private;
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

        String line;
        try{
            FileInputStream fin = openFileInput(HOME_FILENAME);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fin));
            for (int i = 0; (line = bufferedReader.readLine()) != null; i++){
                if (i == 1)
                    home_lng = Double.parseDouble(line);
                else
                    home_lat = Double.parseDouble(line);
            }
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileInputStream fin = openFileInput(FILENAME);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fin));
            for (int i = 0; (line = bufferedReader.readLine()) != null; i++){
                if (i == 0)
                    my_attri = Integer.parseInt(line);
                if (i == 1)
                    is_private = Integer.parseInt(line);
            }
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ((my_attri & 1) == 1)
            ((CheckBox) findViewById(R.id.mycheckBox0)).setChecked(true);
        if ((my_attri & 2) == 2)
            ((CheckBox) findViewById(R.id.mycheckBox1)).setChecked(true);
        if ((my_attri & 4) == 4)
            ((CheckBox) findViewById(R.id.mycheckBox2)).setChecked(true);
        if ((my_attri & 8) == 8)
            ((CheckBox) findViewById(R.id.mycheckBox3)).setChecked(true);
        if ((my_attri & 16) == 16)
            ((CheckBox) findViewById(R.id.mycheckBox4)).setChecked(true);
        if ((my_attri & 32) == 32)
            ((CheckBox) findViewById(R.id.mycheckBox5)).setChecked(true);
        if ((my_attri & 64) == 64)
            ((CheckBox) findViewById(R.id.mycheckBox6)).setChecked(true);
        if ((my_attri & 128) == 128)
            ((CheckBox) findViewById(R.id.mycheckBox7)).setChecked(true);
        if ((my_attri & 256) == 256)
            ((CheckBox) findViewById(R.id.mycheckBox8)).setChecked(true);
        if (is_private == 1)
            ((Switch) findViewById(R.id.switch1)).setChecked(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                is_private = i;
                Log.d("MAIN", "is_private=" + Integer.toString(is_private));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void onMyAttriCheckboxClicked(View view){
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.mycheckBox0:
                my_attri ^= 1;
                break;
            case R.id.mycheckBox1:
                my_attri ^= 2;
                break;
            case R.id.mycheckBox2:
                my_attri ^= 4;
                break;
            case R.id.mycheckBox3:
                my_attri ^= 8;
                break;
            case R.id.mycheckBox4:
                my_attri ^= 16;
                break;
            case R.id.mycheckBox5:
                my_attri ^= 32;
                break;
            case R.id.mycheckBox6:
                my_attri ^= 64;
                break;
            case R.id.mycheckBox7:
                my_attri ^= 128;
                break;
            case R.id.mycheckBox8:
                my_attri ^= 256;
                break;
        }
        Log.d("MAIN", Integer.toString(my_attri));
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
        FileOutputStream fos;
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
            fos.write(Integer.toString(my_attri).getBytes());
            fos.write(newline.getBytes());
            fos.write(Integer.toString(is_private).getBytes());
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
        Log.d("MAIN", "my_attri: " + Integer.toString(my_attri));
        Log.d("MAIN", "is_private: " + Integer.toString(is_private));
        Toast.makeText(this, "We will not disclose your personal information.", Toast.LENGTH_SHORT).show();
        finish();
    }



    public void onSwitchClicked(View view){
        boolean checked = ((Switch)view).isChecked();
        /*if (checked)
            is_private = 1;
        else
            is_private = 0;
        Log.d("MAIN", "is_private=" + Integer.toString(is_private));*/
    }
}

