package com.example.siubo.wheretoyov50;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import java.util.Random;
import java.util.TimeZone;

import static java.lang.Math.sqrt;

public class MainActivity extends AppCompatActivity {
    protected static int my_attri, attri, is_private;
    protected String FILENAME = "user_info";
    protected String HOME_FILENAME = "home_info";
    protected String NOTI_FILENAME = "noti_file";
    protected FusedLocationProviderClient mFusedLocationClient;
    protected LocationRequest mLocationRequest;
    protected LocationCallback mLocationCallback;
    protected DatabaseReference ref;
    protected double home_lat, home_lng, ori_lat, ori_lng, push_lat, push_lng;
    protected int first_stayed, stayed;
    protected Context mContext;
    protected String file_key;
    protected int start;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(homeIntent);
        }
        setContentView(R.layout.activity_main);
        Log.d("MAIN", "Main onCreate.");
        my_attri = 0;
        home_lat = 0.0;
        home_lng = 0.0;
        is_private = 0;
        first_stayed = 1500;
        start = 0;
        mContext = this;

        ref = FirebaseDatabase.getInstance().getReference(getString(R.string.database));
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability){
                super.onLocationAvailability(locationAvailability);
                if (!locationAvailability.isLocationAvailable())
                    first_stayed = 1500;
            }
            @Override
            public void onLocationResult(LocationResult locationResult){
                if (my_attri == 2) {
                    Log.d("MAIN", "my_attri Not Init.");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (home_lat != 0.0 && home_lng != 0.0 && dist(home_lat, home_lng, location.getLatitude(), location.getLongitude()) < 200) {
                        Log.d("MAIN", "Near Home.");
                        return;
                    }

                    if (first_stayed == 1500){
                        ori_lat = location.getLatitude();
                        ori_lng = location.getLongitude();
                        first_stayed = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance().get(Calendar.MINUTE);
                        Log.d("MAIN", "Location Update Init.");
                        return;
                    }

                    if (dist(ori_lat, ori_lng, location.getLatitude(), location.getLongitude()) < 200){
                        Log.d("MAIN", "Still Within 200 Meters Range.");
                        return;
                    }

                    stayed = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance().get(Calendar.MINUTE) - first_stayed;
                    if (stayed < 0)
                        stayed += 1440;

                    if (stayed < 30){
                        ori_lat = location.getLatitude();
                        ori_lng = location.getLongitude();
                        first_stayed = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance().get(Calendar.MINUTE);
                        Log.d("MAIN", "Stayed Less Than 30 Mins.");
                        return;
                    }

                    if (is_private == 1){
                        Random r = new Random();
                        double randomLatDiff = -0.00049 + (0.00049 + 0.00049) * r.nextDouble();
                        double limit = (1 - randomLatDiff * randomLatDiff / 0.0000002401) * 0.0000002025;
                        limit = sqrt(limit);
                        double randomLngDiff = limit * -1 + (limit * 2) * r.nextDouble();
                        push_lat = ori_lat + randomLatDiff;
                        push_lng = ori_lng + randomLngDiff;
                    }
                    else{
                        push_lat = ori_lat;
                        push_lng = ori_lng;
                    }

                    String key = ref.push().getKey();
                    DatabaseItem additem = new DatabaseItem(my_attri, push_lat, push_lng,
                            Integer.toString(stayed / 60) + ":" + Integer.toString(stayed % 60),
                            Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)), is_private);
                    ref.child(key).setValue(additem);

                    Log.d("MAIN", "Added Item to Database.");
                    Log.d("MAIN", "Key: " + key);
                    Log.d("MAIN", "my_attri: " + Integer.toString(my_attri));
                    Log.d("MAIN", "Lat: " + Double.toString(push_lat));
                    Log.d("MAIN", "Lng: " + Double.toString(push_lng));
                    Log.d("MAIN", "Stayed: " + Integer.toString(stayed / 60) + ":" + Integer.toString(stayed % 60));
                    Log.d("MAIN", "Lastseen: " + Integer.toString(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)));
                    Log.d("MAIN", "is_private: " + Integer.toString(is_private));
                    ori_lat = location.getLatitude();
                    ori_lng = location.getLongitude();
                    first_stayed = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance().get(Calendar.MINUTE);
                    Log.d("MAIN", "Reinit Location Update.");

                    try {
                        FileInputStream fin = openFileInput(NOTI_FILENAME);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fin));
                        file_key = bufferedReader.readLine();
                        fin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (file_key != null) {
                        if (file_key.length() > 5) {
                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            notificationManager.cancel(1);
                        }
                    }
                    FileOutputStream fos;
                    String newline = "\n";
                    try {
                        fos = openFileOutput(NOTI_FILENAME, Context.MODE_PRIVATE);
                        fos.write(key.getBytes());
                        fos.write(newline.getBytes());
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent modifyIntent = new Intent(mContext, ModifyMarker.class);
                    Intent deleteIntent = new Intent(mContext, DeleteMarker.class);
                    Intent defaultIntent = new Intent(mContext, DefaultMarker.class);
                    PendingIntent modifyPendingIntent = PendingIntent.getActivity(mContext, 0, modifyIntent, 0);
                    PendingIntent deletePendingIntent = PendingIntent.getActivity(mContext, 0, deleteIntent, 0);
                    PendingIntent defaultPendingIntent = PendingIntent.getActivity(mContext, 0, defaultIntent, 0);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("New attraction found!")
                            .setContentText("Tell us about the place you stayed")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .addAction(R.drawable.ic_launcher_background, "MODIFY", modifyPendingIntent)
                            .addAction(R.drawable.ic_launcher_background, "DELETE", deletePendingIntent)
                            .addAction(R.drawable.ic_launcher_background, "DEFAULT", defaultPendingIntent);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
                    notificationManager.notify(1, mBuilder.build());
                }
            }
        };
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("MAIN", "Main onResume.");

        attri = 0;
        ((CheckBox) findViewById(R.id.checkBox0)).setChecked(false);
        ((CheckBox) findViewById(R.id.checkBox1)).setChecked(false);
        ((CheckBox) findViewById(R.id.checkBox2)).setChecked(false);
        ((CheckBox) findViewById(R.id.checkBox3)).setChecked(false);
        ((CheckBox) findViewById(R.id.checkBox4)).setChecked(false);
        ((CheckBox) findViewById(R.id.checkBox5)).setChecked(false);
        ((CheckBox) findViewById(R.id.checkBox6)).setChecked(false);
        ((CheckBox) findViewById(R.id.checkBox7)).setChecked(false);
        ((CheckBox) findViewById(R.id.checkBox8)).setChecked(false);

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
                    my_attri = Integer.parseInt(line);
                if (i == 1)
                    is_private = Integer.parseInt(line);
            }
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void createLocationRequest(){
        /*
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setSmallestDisplacement(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        */
        ///*
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1200000);
        mLocationRequest.setFastestInterval(1200000);
        mLocationRequest.setSmallestDisplacement(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
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

    public void onAttriCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.checkBox0:
                if (checked) attri += 1;
                else attri -= 1;
                break;
            case R.id.checkBox1:
                if (checked) attri += 2;
                else attri -= 2;
                break;
            case R.id.checkBox2:
                if (checked) attri += 4;
                else attri -= 4;
                break;
            case R.id.checkBox3:
                if (checked) attri += 8;
                else attri -= 8;
                break;
            case R.id.checkBox4:
                if (checked) attri += 16;
                else attri -= 16;
                break;
            case R.id.checkBox5:
                if (checked) attri += 32;
                else attri -= 32;
                break;
            case R.id.checkBox6:
                if (checked) attri += 64;
                else attri -= 64;
                break;
            case R.id.checkBox7:
                if (checked) attri += 128;
                else attri -= 128;
                break;
            case R.id.checkBox8:
                if (checked) attri += 256;
                else attri -= 256;
                break;
        }
    }

    public void onButtonClicked(View view){
        if (my_attri != 0 && attri != 0){
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("ATTRI", attri);
            startActivity(intent);
        }
        else if (my_attri == 0)
            Toast.makeText(this, "Provide information before using the map.", Toast.LENGTH_SHORT).show();
        else if (attri == 0)
            Toast.makeText(this, "Choose what you want to search for.", Toast.LENGTH_SHORT).show();
    }

    public void onSettingsButtonClicked(View view){
        Intent intent = new Intent(this, com.example.siubo.wheretoyov50.Settings.class);
        intent.putExtra("MY_ATTRI", my_attri);
        intent.putExtra("IS_PRIVATE", is_private);
        intent.putExtra("HOME_LAT", home_lat);
        intent.putExtra("HOME_LNG", home_lng);
        startActivity(intent);
    }

    public void onNotiButtonClicked(View view){
        String key = ref.push().getKey();
        DatabaseItem additem = new DatabaseItem(1, 22.27319, 114.12867,
                Integer.toString(stayed / 60) + ":" + Integer.toString(stayed % 60),
                Integer.toString(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)), is_private);
        ref.child(key).setValue(additem);

        Log.d("MAIN", "Added Item to Database.");
        Log.d("MAIN", "Key: " + key);
        Log.d("MAIN", "my_attri: " + Integer.toString(my_attri));
        Log.d("MAIN", "Lat: " + Double.toString(ori_lat));
        Log.d("MAIN", "Lng: " + Double.toString(ori_lng));
        Log.d("MAIN", "Stayed: " + Integer.toString(stayed / 60) + ":" + Integer.toString(stayed % 60));
        Log.d("MAIN", "Lastseen: " + Integer.toString(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)));

        try {
            FileInputStream fin = openFileInput(NOTI_FILENAME);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fin));
            file_key = bufferedReader.readLine();
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file_key != null) {
            if (file_key.length() > 5) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(1);
            }
        }
        FileOutputStream fos;
        String newline = "\n";
        try {
            fos = openFileOutput(NOTI_FILENAME, Context.MODE_PRIVATE);
            fos.write(key.getBytes());
            fos.write(newline.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent modifyIntent = new Intent(mContext, ModifyMarker.class);
        Intent deleteIntent = new Intent(mContext, DeleteMarker.class);
        Intent defaultIntent = new Intent(mContext, DefaultMarker.class);
        PendingIntent modifyPendingIntent = PendingIntent.getActivity(mContext, 0, modifyIntent, 0);
        PendingIntent deletePendingIntent = PendingIntent.getActivity(mContext, 0, deleteIntent, 0);
        PendingIntent defaultPendingIntent = PendingIntent.getActivity(mContext, 0, defaultIntent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New attraction found!")
                .setContentText("Tell us about the place you stayed")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_launcher_background, "MODIFY", modifyPendingIntent)
                .addAction(R.drawable.ic_launcher_background, "DELETE", deletePendingIntent)
                .addAction(R.drawable.ic_launcher_background, "DEFAULT", defaultPendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(1, mBuilder.build());
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
