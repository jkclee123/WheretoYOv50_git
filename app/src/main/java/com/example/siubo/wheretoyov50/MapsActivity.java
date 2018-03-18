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
    protected int attri;
    protected ClusterManager<MyItem> mClusterManager;
    protected DefaultClusterRenderer<MyItem> mDefaultClusterRenderer;
    protected DatabaseReference ref;
    protected String lastseen, stayed_title, diff_snippet;

    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MAIN", "Maps onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        attri = intent.getExtras().getInt("ATTRI");
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
        ref = FirebaseDatabase.getInstance().getReference(getString(R.string.database));
        Log.d("MAIN", "Looking For attri = " + Integer.toString(attri) + " Markers.");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long getattri;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    getattri = (long) ds.child("attri").getValue();
                    if ((getattri & attri) != attri)
                        continue;
                    lastseen = (String) ds.child("lastseen").getValue();
                    int diff = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - Integer.parseInt(lastseen);
                    if (diff == 0)
                        diff_snippet = "Today";
                    else if (diff <= 1)
                        diff_snippet = Integer.toString(diff) + " day ago";
                    else if (diff < 7)
                        diff_snippet = Integer.toString(diff) + " days ago";
                    else if (diff < 14)
                        diff_snippet = Integer.toString(diff / 7) + " week ago";
                    else
                        diff_snippet = Integer.toString(diff / 7) + " weeks ago";
                    String[] stayed_time = ((String) ds.child("hour").getValue()).split(":", -1);
                    stayed_title = "Stayed " + stayed_time[0] + "hr " + stayed_time[1] + "min";
                    MyItem marker = new MyItem( (double) ds.child("lat").getValue(),
                            (double) ds.child("lng").getValue(), stayed_title, diff_snippet);
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
