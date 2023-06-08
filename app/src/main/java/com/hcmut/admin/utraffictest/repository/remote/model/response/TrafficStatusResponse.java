package com.hcmut.admin.utraffictest.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;
import com.hcmut.admin.utraffictest.model.PolylineResponse;

public class TrafficStatusResponse {
    @SerializedName("segment")
    private String segmentId;
    @SerializedName("velocity")
    private String velocity;
    @SerializedName("color")
    private String color;
    @SerializedName("polyline")
    private PolylineResponse polylineResponse;

    public String getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(String segmentId) {
        this.segmentId = segmentId;
    }

    public String getVelocity() {
        return velocity;
    }

    public void setVelocity(String velocity) {
        this.velocity = velocity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public PolylineResponse getPolylineResponse() {
        return polylineResponse;
    }

    public void setPolylineResponse(PolylineResponse polylineResponse) {
        this.polylineResponse = polylineResponse;
    }
}
