package com.hcmut.admin.utraffictest.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 10/5/2018.
 */

public class RouteSegment {
    private String placeId;
    private List<LatLng> points;

    public RouteSegment(String placeId, LatLng point) {
        this.placeId = placeId;
        points = new ArrayList<>();
        points.add(point);
    }

    boolean checkIfMatchPlaceId(String placeId) {
        return this.placeId.equals(placeId);
    }

    void addTrafficData(LatLng point) {
        points.add(point);
    }
}
