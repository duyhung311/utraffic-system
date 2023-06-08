package com.hcmut.admin.utraffictest.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Admin on 10/5/2018.
 */

public class TrafficData {
    private LatLng center;
    private float radius;
    private float currentVelocity;
    private int trafficLevel;
    private int duration;
    private String placeId;


    public TrafficData(LatLng center, float radius, float currentVelocity, int trafficLevel, int duration, String placeId) {
        this.center = center;
        this.radius = radius;
        this.currentVelocity = currentVelocity;
        this.trafficLevel = trafficLevel;
        this.duration = duration;
        this.placeId = placeId;
    }

    public LatLng getCenter() {
        return center;
    }

    public float getRadius() {
        return radius;
    }

    public float getCurrentVelocity() {
        return currentVelocity;
    }

    public int getTrafficLevel() {
        return trafficLevel;
    }

    public int getDuration() {
        return duration;
    }

    public String getPlaceId() {
        return placeId;
    }
}
