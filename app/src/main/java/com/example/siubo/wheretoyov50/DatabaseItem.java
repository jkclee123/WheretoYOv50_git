package com.example.siubo.wheretoyov50;

/**
 * Created by SiuBo on 21-Nov-17.
 */

public class DatabaseItem {
    public long attri;
    public double lat;
    public double lng;
    public String hour;
    public String lastseen;
    public int is_private;

    public DatabaseItem (long attri, double lat, double lng, String hour, String lastseen, int is_private){
        this.attri = attri;
        this.lat = lat;
        this.lng = lng;
        this.hour = hour;
        this.lastseen = lastseen;
        this.is_private = is_private;
    }
}
