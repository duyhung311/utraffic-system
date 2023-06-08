package com.hcmut.admin.utraffictest.repository.remote.model.response;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.hcmut.admin.utraffictest.model.PolylineResponse;

public class NearSegmentResponse {
    @SerializedName("_id")
    private int segmentId;
    @SerializedName("street")
    private String streetId;
    @SerializedName("length")
    private int length;
    @SerializedName("polyline")
    private PolylineResponse polylineResponse;

    public int getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }

    public String getStreetId() {
        return streetId;
    }

    public void setStreetId(String streetId) {
        this.streetId = streetId;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public PolylineResponse getPolylineResponse() {
        return polylineResponse;
    }

    public void setPolylineResponse(PolylineResponse polylineResponse) {
        this.polylineResponse = polylineResponse;
    }

    public LatLng getStartLatLng() {
        return polylineResponse.getStart();
    }

    public LatLng getEndLatLng() {
        return polylineResponse.getEnd();
    }
}
