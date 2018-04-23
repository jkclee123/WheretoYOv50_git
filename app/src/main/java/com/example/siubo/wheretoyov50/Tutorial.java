package com.example.siubo.wheretoyov50;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;



public class Tutorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

    public void SettingsClick(View view) {
        Intent settingIntent = new Intent(this, t_settings.class);
        startActivity(settingIntent);
    }

    public void MainClick(View view) {
        Intent mainIntent = new Intent(this, t_main.class);
        startActivity(mainIntent);
    }

    public void MapClick(View view) {
        Intent mapIntent = new Intent(this, t_map.class);
        startActivity(mapIntent);
    }

    public void SearchClick(View view) {
        Intent searchIntent = new Intent(this, t_web.class);
        startActivity(searchIntent);
    }
}
