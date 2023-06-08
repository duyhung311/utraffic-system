package com.hcmut.admin.utraffictest.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Admin on 11/15/2018.
 */

public class TrafficSegment {
    private String id;
    private LatLng start;
    private LatLng end;
    private int speed;
    private int status;
    private String roadName;


    public TrafficSegment(String id, LatLng start, LatLng end, int status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public LatLng getStart() {
        return start;
    }

    public LatLng getEnd() {
        return end;
    }

    public int getSpeed() {
        return speed;
    }

    public int getStatus() {
        return status;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStart(LatLng start) {
        this.start = start;
    }

    public void setEnd(LatLng end) {
        this.end = end;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }
}
