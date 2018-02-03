package com.example.siubo.wheretoyov50;

/**
 * Created by SiuBo on 21-Nov-17.
 */

public class DatabaseItem {
    public long gender;
    public double lat;
    public double lng;
    public String hour;
    public String lastseen;

    public DatabaseItem (long gender, double lat, double lng, String hour, String lastseen){
        this.gender = gender;
        this.lat = lat;
        this.lng = lng;
        this.hour = hour;
        this.lastseen = lastseen;
    }
}
