package com.example.maprace;

import org.osmdroid.util.GeoPoint;

public class Landmark {
    private String name;
    private GeoPoint location;

    Landmark(String name, GeoPoint location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return this.name;
    }

    public GeoPoint getLocation() {
        return this.location;
    }
}
