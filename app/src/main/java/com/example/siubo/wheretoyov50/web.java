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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class web extends AppCompatActivity {
    private WebView webView;
    ProgressBar bar;
    private DrawerLayout mDrawerLayout;
    protected Context mContext;
    protected int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.menuicon);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mContext = this;

        bar= (ProgressBar)findViewById(R.id.progressBar2);
        bar.setVisibility(View.INVISIBLE);

        webView = (WebView) findViewById(R.id.web1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url){
                super.onPageFinished(webView, url);
                bar.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);
                Log.d("MAIN", "Loaded");
            }
        });

        findViewById(R.id.button8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (page == 1)
                    return;
                page = 1;
                String url = "http://www.weekendhk.com";
                load(url);
            }
        });

        findViewById(R.id.button9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (page == 2)
                    return;
                page = 2;
                String url = "http://etw.nextdigital.com.hk";
                load(url);
            }
        });

        findViewById(R.id.button10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (page == 3)
                    return;
                page = 3;
                String url = "http://www.ulifestyle.com.hk";
                load(url);
            }
        });


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
                            case R.id.help:
                                Intent intent1 = new Intent(mContext, com.example.siubo.wheretoyov50.Tutorial.class);
                                startActivity(intent1);
                                finish();
                                break;
                            case R.id.home:
                                Intent intent2 = new Intent(mContext, com.example.siubo.wheretoyov50.MainActivity.class);
                                startActivity(intent2);
                                finish();
                                break;
                            case R.id.about:
                                Intent intent3 = new Intent(mContext, com.example.siubo.wheretoyov50.about.class);
                                startActivity(intent3);
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    public void load(String url){
        bar.setVisibility(View.VISIBLE);
        webView.setVisibility(View.INVISIBLE);
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed(){
        if(webView.canGoBack()){
            webView.goBack();
        }
        else{
            super.onBackPressed();
        }
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
