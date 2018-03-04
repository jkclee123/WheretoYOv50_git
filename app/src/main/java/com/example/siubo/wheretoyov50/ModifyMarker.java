package com.example.siubo.wheretoyov50;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by SiuBo on 04-Mar-18.
 */

public class ModifyMarker extends AppCompatActivity{
    protected String NOTI_FILENAME = "noti_file";
    protected DatabaseReference ref;
    protected String key;
    protected int my_attri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        my_attri = 0;
        ref = FirebaseDatabase.getInstance().getReference("haha");

        try {
            FileInputStream fin = openFileInput(NOTI_FILENAME);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fin));
            key = bufferedReader.readLine();
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onMyAttriCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.mycheckBox0:
                if (checked) my_attri += 1;
                else my_attri -= 1;
                break;
            case R.id.mycheckBox1:
                if (checked) my_attri += 2;
                else my_attri -= 2;
                break;
            case R.id.mycheckBox2:
                if (checked) my_attri += 4;
                else my_attri -= 4;
                break;
            case R.id.mycheckBox3:
                if (checked) my_attri += 8;
                else my_attri -= 8;
                break;
            case R.id.mycheckBox4:
                if (checked) my_attri += 16;
                else my_attri -= 16;
                break;
            case R.id.mycheckBox5:
                if (checked) my_attri += 32;
                else my_attri -= 32;
                break;
            case R.id.mycheckBox6:
                if (checked) my_attri += 64;
                else my_attri -= 64;
                break;
            case R.id.mycheckBox7:
                if (checked) my_attri += 128;
                else my_attri -= 128;
                break;
            case R.id.mycheckBox8:
                if (checked) my_attri += 256;
                else my_attri -= 256;
                break;
        }
        Log.d("MAIN", Integer.toString(my_attri));
    }

    public void onModifyButtonPressed(View view) {
        if (my_attri == 0)
            return;
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
        finish();
    }
}
