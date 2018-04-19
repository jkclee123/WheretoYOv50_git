package com.example.siubo.wheretoyov50;

import android.content.Context;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Calendar;
import java.util.Random;

import static java.lang.Math.sqrt;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    protected GoogleMap mMap;
    protected int attri;
    protected ClusterManager<MyItem> mClusterManager;
    protected CustomClusterRenderer renderer;
    protected DatabaseReference ref;
    protected String lastseen, stayed_title, diff_snippet;
    public int is_private;
    protected double push_lat, push_lng;
    protected MyItem marker;

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
        is_private = intent.getExtras().getInt("ATTRI");
        is_private = intent.getExtras().getInt("IS_PRIVATE");
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
        renderer = new CustomClusterRenderer(this, mMap, mClusterManager);
        mClusterManager.setRenderer(renderer);
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
                    if (is_private == 1) {
                        Random r = new Random();
                        double randomLatDiff = -0.00049 + (0.00049 + 0.00049) * r.nextDouble();
                        double limit = (1 - randomLatDiff * randomLatDiff / 0.0000002401) * 0.0000002025;
                        limit = sqrt(limit);
                        double randomLngDiff = limit * -1 + (limit * 2) * r.nextDouble();
                        push_lat = (double) ds.child("lat").getValue() + randomLatDiff;
                        push_lng = (double) ds.child("lng").getValue() + randomLngDiff;
                    }
                    else{
                        push_lat = (double) ds.child("lat").getValue();
                        push_lng = (double) ds.child("lng").getValue();
                    }
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
                    marker = new MyItem(push_lat, push_lng, stayed_title, diff_snippet, (long) ds.child("is_private").getValue());
                    mClusterManager.addItem(marker);
                    if (is_private == 1) {
                        marker = new MyItem((double) ds.child("lat").getValue(), (double) ds.child("lng").getValue(), stayed_title, diff_snippet, 0);
                        mClusterManager.addItem(marker);
                    }
                }
                getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.3964, 114.1095), 10));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public class CustomClusterRenderer extends DefaultClusterRenderer<MyItem> {
        private final Context mContext;

        public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
            super(context, map, clusterManager);
            mContext = context;
        }

        @Override protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
            final BitmapDescriptor markerDescriptor;
            Log.d("MAIN", "is_private: " + Integer.toString(is_private) + "\nitem.getIs_private: " + Long.toString(item.getIs_private()));
            if (is_private == 1){
                if (item.getIs_private() == 1)
                     markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
                else
                    markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            }
            else{
                if (item.getIs_private() == 1)
                    markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                else
                    markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            }
            markerOptions.icon(markerDescriptor);
        }
    }
}
