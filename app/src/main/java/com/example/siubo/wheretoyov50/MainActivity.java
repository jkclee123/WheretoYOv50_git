package com.example.siubo.wheretoyov50;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    protected static int gender;
    protected static int iamgender;
    protected String FILENAME = "user_info";
    protected String HOME_FILENAME = "home_info";
    protected FusedLocationProviderClient mFusedLocationClient;
    protected LocationRequest mLocationRequest;
    protected LocationCallback mLocationCallback;
    protected DatabaseReference ref;
    protected double home_lat, home_lng, ori_lat, ori_lng;
    protected int first_stayed, stayed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MAIN", "Main onCreate.");
        iamgender = 2;
        gender = 2;
        home_lat = 0.0;
        home_lng = 0.0;
        first_stayed = 1500;


        ref = FirebaseDatabase.getInstance().getReference("data");
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                if (iamgender == 2) {
                    Log.d("MAIN", "iamgender Not Init.");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (home_lat != 0.0 && home_lng != 0.0 && dist(home_lat, home_lng, location.getLatitude(), location.getLongitude()) < 200) {
                        Log.d("MAIN", "Near Home.");
                        return;
                    }
                    stayed = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance().get(Calendar.MINUTE) - first_stayed;
                    if (stayed < 0)
                        stayed += 1440;
                    if (stayed > 30 && first_stayed != 1500) {
                        DatabaseItem additem = new DatabaseItem(iamgender, ori_lat, ori_lng,
                                Integer.toString(stayed / 60) + ":" + Integer.toString(stayed % 60),
                                Integer.toString(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)));
                        String key = ref.push().getKey();
                        ref.child(key).setValue(additem);
                        Log.d("MAIN", "Added Item to Database.");
                        Log.d("MAIN", "Key: " + key);
                        Log.d("MAIN", "iamgender: " + Integer.toString(iamgender));
                        Log.d("MAIN", "Lat: " + Double.toString(ori_lat));
                        Log.d("MAIN", "Lng: " + Double.toString(ori_lng));
                        Log.d("MAIN", "Stayed: " + Integer.toString(stayed / 60) + ":" + Integer.toString(stayed % 60));
                        Log.d("MAIN", "Lastseen: " + Integer.toString(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)));
                    }
                    ori_lat = location.getLatitude();
                    ori_lng = location.getLongitude();
                    first_stayed = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance().get(Calendar.MINUTE);

                }
            }
        };
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("MAIN", "Main onResume.");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("MAIN", "Requesting Permission...");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 10);
                return;
            } else {
                Log.d("MAIN", "Success");
                getSettings();
            }
        } else {
            Log.d("MAIN", "Success");
            getSettings();
        }
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
                    iamgender = Integer.parseInt(line);
            }
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void createLocationRequest(){
        /*
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        */
        ///*
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1200000);
        mLocationRequest.setFastestInterval(1200000);
        mLocationRequest.setSmallestDisplacement(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //*/

    }

    public void getSettings(){
        createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (mFusedLocationClient == null) {
                    Log.d("MAIN", "Start Location Updates.");
                    startLocationUpdates();
                }
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        Log.d("MAIN", "Exit Settings.");
    }

    public void startLocationUpdates(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
        }
    }

    public void onGenderCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.male_checkBox:
                if (checked){
                    ((CheckBox) view).setChecked(true);
                    ((CheckBox) findViewById(R.id.female_checkBox)).setChecked(false);
                    gender = 1;
                }
                else{
                    ((CheckBox) view).setChecked(false);
                    ((CheckBox) findViewById(R.id.female_checkBox)).setChecked(false);
                    gender = 2;
                }
                break;
            case R.id.female_checkBox:
                if (checked){
                    ((CheckBox) view).setChecked(true);
                    ((CheckBox) findViewById(R.id.male_checkBox)).setChecked(false);
                    gender = 0;
                }
                else{
                    ((CheckBox) view).setChecked(false);
                    ((CheckBox) findViewById(R.id.male_checkBox)).setChecked(false);
                    gender = 2;
                }
                break;
        }
    }

    public void onButtonClicked(View view){
        if (iamgender != 2 && gender != 2){
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("GENDER", gender);
            startActivity(intent);
        }
        else if (iamgender == 2)
            Toast.makeText(this, "Provide information before using the map.", Toast.LENGTH_SHORT).show();
        else if (gender == 2)
            Toast.makeText(this, "Choose what you want to search for.", Toast.LENGTH_SHORT).show();
    }

    public void onSettingsButtonClicked(View view){
        Intent intent = new Intent(this, com.example.siubo.wheretoyov50.Settings.class);
        intent.putExtra("IAMGENDER", iamgender);
        intent.putExtra("HOME_LAT", home_lat);
        intent.putExtra("HOME_LNG", home_lng);
        startActivity(intent);
    }

    public double dist(double lat1, double lng1, double lat2, double lng2){
        LatLng latlng1 = new LatLng(lat1, lng1);
        LatLng latlng2 = new LatLng(lat2, lng2);

        Location location1 = new Location("point 1");
        location1.setLatitude(latlng1.latitude);
        location1.setLongitude(latlng1.longitude);
        Location location2 = new Location("point 2");
        location2.setLatitude(latlng2.latitude);
        location2.setLongitude(latlng2.longitude);

        return location1.distanceTo(location2);
    }


}
