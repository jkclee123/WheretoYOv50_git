package com.example.siubo.wheretoyov50;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by SiuBo on 21-Nov-17.
 */

public class MyItem implements ClusterItem {
    private LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private String mComment;

    public MyItem(LatLng latlng) {
        mPosition = latlng;
        mTitle = "";
        mSnippet = "";
    }

    public MyItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
        mTitle = "";
        mSnippet = "";
    }

    public MyItem(double lat, double lng, String hour, String lastseen) {
        mPosition = new LatLng(lat, lng);
        mTitle = hour;
        mSnippet = lastseen;
    }

    public MyItem(double lat, double lng, String hour, String lastseen, String comment) {
        mPosition = new LatLng(lat, lng);
        mTitle = hour;
        mSnippet = lastseen;
        mComment = comment;
    }

    public MyItem(LatLng latlng, String title, String snippet) {
        mPosition = latlng;
        mTitle = title;
        mSnippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}