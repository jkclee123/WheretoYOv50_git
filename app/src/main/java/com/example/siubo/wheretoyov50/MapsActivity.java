package com.example.siubo.wheretoyov50;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Calendar;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    protected GoogleMap mMap;
    protected int gender;
    protected ClusterManager<MyItem> mClusterManager;
    protected DefaultClusterRenderer<MyItem> mDefaultClusterRenderer;
    protected DatabaseReference ref;
    protected String lastseen, week_snippet, weeks, stayed_title;
    protected int week_cal;

    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MAIN", "Maps onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        gender = intent.getExtras().getInt("GENDER");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.3964, 114.1095), 9));
        mClusterManager = new ClusterManager<MyItem>(this, getMap());
        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        ref = FirebaseDatabase.getInstance().getReference("haha");
        Log.d("MAIN", "Looking For Gender = " + Integer.toString(gender) + " Markers.");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long getgender;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    getgender = (long) ds.child("gender").getValue();
                    if (getgender != gender)
                        continue;
                    lastseen = (String) ds.child("lastseen").getValue();
                    week_cal = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) - Integer.parseInt(lastseen);
                    if (week_cal < 0)
                        week_cal += 52;
                    if (week_cal == 0)
                        week_snippet = "This week";
                    else {
                        if (week_cal == 1)
                            weeks = "week";
                        else
                            weeks = "weeks";
                        week_snippet = Integer.toString(week_cal) + " " + weeks + " ago";
                    }
                    stayed_title = "Stayed " + ds.child("hour").getValue();
                    MyItem marker = new MyItem( (double) ds.child("lat").getValue(),
                            (double) ds.child("lng").getValue(), stayed_title, week_snippet);
                    mClusterManager.addItem(marker);
                }
                getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.3964, 114.1095), 10));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
