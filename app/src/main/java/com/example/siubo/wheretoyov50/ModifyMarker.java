package com.example.siubo.wheretoyov50;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

/**
 * Created by SiuBo on 04-Mar-18.
 */

public class ModifyMarker extends AppCompatActivity{
    protected String NOTI_FILENAME = "noti_file";
    protected DatabaseReference ref;
    protected String key;
    protected int my_attri;
    protected String gethour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
        my_attri = 0;
        ref = FirebaseDatabase.getInstance().getReference(getString(R.string.database));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            FileInputStream fin = openFileInput(NOTI_FILENAME);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fin));
            key = bufferedReader.readLine();
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(key)){
                        gethour = ((String) ds.child("hour").getValue());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void onMyAttriCheckboxClicked(View view) {
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

    public void onModifyButtonPressed(View view) {
        if (my_attri == 0) {
            Toast.makeText(this, "Modify attributes before submit.", Toast.LENGTH_SHORT).show();
            return;
        }
        ref.child(key).child("attri").setValue(my_attri);
        FileOutputStream fos;
        String newline = "\n";
        try {
            fos = openFileOutput(NOTI_FILENAME, Context.MODE_PRIVATE);
            fos.write(newline.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
        Toast.makeText(this, "Location Modified.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
