package com.example.siubo.wheretoyov50;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class HomeActivity extends AppCompatActivity {
    private static long SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
            }
        }, SPLASH_TIME_OUT);
        Log.d("MAIN", "HOme");
        return;
    }
}
