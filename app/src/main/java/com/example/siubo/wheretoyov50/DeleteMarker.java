package com.example.siubo.wheretoyov50;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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

public class DeleteMarker extends AppCompatActivity{
    protected String NOTI_FILENAME = "noti_file";
    protected DatabaseReference ref;
    protected String key;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ref = FirebaseDatabase.getInstance().getReference(getString(R.string.database));

        try {
            FileInputStream fin = openFileInput(NOTI_FILENAME);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fin));
            key = bufferedReader.readLine();
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ref.child(key).removeValue();
        Log.d("MAIN", "delete " + key);

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
        Toast.makeText(this, "Location deleted.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
