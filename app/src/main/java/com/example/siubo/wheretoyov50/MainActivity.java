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
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
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
    protected View mview;
    private PrefManager prefManager;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(this);
        /*
        if (!prefManager.isFirstTimeLaunch()) {
            Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(homeIntent);
        }
        */

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.menuicon);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        Log.d("MAIN", "Main onCreate.");
        my_attri = 0;
        home_lat = 0.0;
        home_lng = 0.0;
        is_private = 0;
        first_stayed = 1500;
        start = 0;
        mContext = this;
        mview = findViewById(R.id.imageButton1);
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

                    /*
                    if (dist(ori_lat, ori_lng, location.getLatitude(), location.getLongitude()) < 200){
                        Log.d("MAIN", "Still Within 200 Meters Range.");
                        return;
                    }
                    */

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

                    addNoise();
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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(false);
                        switch(menuItem.getItemId()){
                            case R.id.settings:
                                onSettingsButtonClicked(mview);
                                break;
                            case R.id.web:
                                WebClick(mview);
                                finish();
                                break;
                            case R.id.help:
                                TutorClick(mview);
                                finish();
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("MAIN", "Main onResume.");

        attri = 0;
        ((ImageButton)findViewById(R.id.imageButton1)).setImageResource(R.drawable.hiking);
        ((ImageButton)findViewById(R.id.imageButton2)).setImageResource(R.drawable.shopping);
        ((ImageButton)findViewById(R.id.imageButton3)).setImageResource(R.drawable.sports);
        ((ImageButton)findViewById(R.id.imageButton4)).setImageResource(R.drawable.videogames);
        ((ImageButton)findViewById(R.id.imageButton5)).setImageResource(R.drawable.concert);
        ((ImageButton)findViewById(R.id.imageButton6)).setImageResource(R.drawable.drama);
        ((ImageButton)findViewById(R.id.imageButton7)).setImageResource(R.drawable.band);
        ((ImageButton)findViewById(R.id.imageButton8)).setImageResource(R.drawable.karaoke);
        ((ImageButton)findViewById(R.id.imageButton9)).setImageResource(R.drawable.watersports);

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
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1200000);
        mLocationRequest.setFastestInterval(1200000);
        mLocationRequest.setSmallestDisplacement(200);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
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

    public void onButtonClicked(View view){
        if (my_attri != 0 && attri != 0){
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("IS_PRIVATE", is_private);
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
        startActivity(intent);
    }

    public void TutorClick(View view) {
        Intent intent = new Intent(this, Tutorial.class);
        startActivity(intent);
    }

    public void WebClick(View view){
        Intent intent = new Intent(this, web.class);
        startActivity(intent);
    }

    public void onNotiButtonClicked(View view){
        String key = ref.push().getKey();
        DatabaseItem additem = new DatabaseItem(1, 22.27319, 114.12867,
                Integer.toString(stayed / 60) + ":" + Integer.toString(stayed % 60),
                Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)), is_private);
        ref.child(key).setValue(additem);

        Log.d("MAIN", "Added Item to Database.");
        Log.d("MAIN", "Key: " + key);
        Log.d("MAIN", "my_attri: " + Integer.toString(my_attri));
        Log.d("MAIN", "Lat: " + Double.toString(ori_lat));
        Log.d("MAIN", "Lng: " + Double.toString(ori_lng));
        Log.d("MAIN", "Stayed: " + Integer.toString(stayed / 60) + ":" + Integer.toString(stayed % 60));
        Log.d("MAIN", "Lastseen: " + Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)));

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

    public void onImageButtonClick(View view){
        switch(view.getId()){
            case R.id.imageButton1:
                Log.d("MAIN", "Button 1 pressed");
                attri ^= 1;
                if((attri & 1) == 1)
                    ( (ImageButton) view).setImageResource(R.drawable.hiking_checked);
                else
                    ( (ImageButton) view).setImageResource(R.drawable.hiking);
                break;
            case R.id.imageButton2:
                Log.d("MAIN", "Button 2 pressed");
                attri ^= 2;
                if((attri & 2) == 2)
                    ( (ImageButton) view).setImageResource(R.drawable.shopping_checked);
                else
                    ( (ImageButton) view).setImageResource(R.drawable.shopping);
                break;
            case R.id.imageButton3:
                Log.d("MAIN", "Button 3 pressed");
                attri ^= 4;
                if((attri & 4) == 4)
                    ( (ImageButton) view).setImageResource(R.drawable.sports_checked);
                else
                    ( (ImageButton) view).setImageResource(R.drawable.sports);
                break;
            case R.id.imageButton4:
                Log.d("MAIN", "Button 4 pressed");
                attri ^= 8;
                if((attri & 8) == 8)
                    ( (ImageButton) view).setImageResource(R.drawable.videogames_checked);
                else
                    ( (ImageButton) view).setImageResource(R.drawable.videogames);
                break;
            case R.id.imageButton5:
                Log.d("MAIN", "Button 5 pressed");
                attri ^= 16;
                if((attri & 16) == 16)
                    ( (ImageButton) view).setImageResource(R.drawable.concert_checked);
                else
                    ( (ImageButton) view).setImageResource(R.drawable.concert);
                break;
            case R.id.imageButton6:
                Log.d("MAIN", "Button 6 pressed");
                attri ^= 32;
                if((attri & 32) == 32)
                    ( (ImageButton) view).setImageResource(R.drawable.drama_checked);
                else
                    ( (ImageButton) view).setImageResource(R.drawable.drama);
                break;
            case R.id.imageButton7:
                Log.d("MAIN", "Button 7 pressed");
                attri ^= 64;
                if((attri & 64) == 64)
                    ( (ImageButton) view).setImageResource(R.drawable.band_checked);
                else
                    ( (ImageButton) view).setImageResource(R.drawable.band);
                break;
            case R.id.imageButton8:
                Log.d("MAIN", "Button 8 pressed");
                attri ^= 128;
                if((attri & 128) == 128)
                    ( (ImageButton) view).setImageResource(R.drawable.karaoke_checked);
                else
                    ( (ImageButton) view).setImageResource(R.drawable.karaoke);
                break;
            case R.id.imageButton9:
                Log.d("MAIN", "Button 9 pressed");
                attri ^= 256;
                if((attri & 256) == 256)
                    ( (ImageButton) view).setImageResource(R.drawable.watersports_checked);
                else
                    ( (ImageButton) view).setImageResource(R.drawable.watersports);
                break;
        }
        Log.d("MAIN", "attri: " + Integer.toString(attri));
    }

    public void addNoise(){
        double latbound = 0.00049 * is_private;
        double lngbound = 0.00045 * is_private;
        Random r = new Random();
        double randomLatDiff = -1 * latbound + latbound * 2 * r.nextDouble();
        double limit = (1 - randomLatDiff * randomLatDiff / latbound / latbound) * lngbound * lngbound;
        limit = sqrt(limit);
        double randomLngDiff = limit * -1 + (limit * 2) * r.nextDouble();
        push_lat = ori_lat + randomLatDiff;
        push_lng = ori_lng + randomLngDiff;
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
