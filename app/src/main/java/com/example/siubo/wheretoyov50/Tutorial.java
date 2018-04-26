package com.example.siubo.wheretoyov50;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;


public class Tutorial extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.menuicon);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mContext = this;

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(false);
                        switch(menuItem.getItemId()){
                            case R.id.settings:
                                Intent intent = new Intent(mContext, com.example.siubo.wheretoyov50.Settings.class);
                                startActivity(intent);
                                break;
                            case R.id.web:
                                Intent intent1 = new Intent(mContext, com.example.siubo.wheretoyov50.web.class);
                                finish();
                                break;
                            case R.id.home:
                                Intent intent2 = new Intent(mContext, com.example.siubo.wheretoyov50.MainActivity.class);
                                startActivity(intent2);
                                finish();
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
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
