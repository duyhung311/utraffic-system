package com.hcmut.admin.utraffictest.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Admin on 11/15/2018.
 */

public class Cell {
    String id;
    LatLng latLng;
    ArrayList<TrafficSegment> trafficSegmentsList;

    public Cell(String id, LatLng latLng, ArrayList<TrafficSegment> trafficSegmentsList) {
        this.id = id;
        this.latLng = latLng;
        this.trafficSegmentsList = new ArrayList<>(trafficSegmentsList);
    }

    public String getId() {
        return id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public ArrayList<TrafficSegment> getTrafficSegmentsList() {
        return trafficSegmentsList;
    }
}
