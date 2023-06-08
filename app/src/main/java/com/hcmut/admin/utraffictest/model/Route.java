package com.hcmut.admin.utraffictest.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Admin on 9/28/2018.
 */

public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
