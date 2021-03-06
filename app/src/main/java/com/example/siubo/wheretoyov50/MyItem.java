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
    private long is_private;

    public MyItem(double lat, double lng, String hour, String lastseen, long is_private) {
        mPosition = new LatLng(lat, lng);
        mTitle = hour;
        mSnippet = lastseen;
        this.is_private = is_private;
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

    public long getIs_private() { return is_private; }
}