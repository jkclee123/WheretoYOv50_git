package com.example.siubo.wheretoyov50;

/**
 * Created by SiuBo on 21-Nov-17.
 */

public class DatabaseItem {
    protected long gender;
    protected double lat;
    protected double lng;
    protected String hour;
    protected String lastseen;

    public DatabaseItem (long gender, double lat, double lng, String hour, String lastseen){
        this.gender = gender;
        this.lat = lat;
        this.lng = lng;
        this.hour = hour;
        this.lastseen = lastseen;
    }
}
