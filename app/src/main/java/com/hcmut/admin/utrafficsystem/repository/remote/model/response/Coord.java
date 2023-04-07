package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

public class Coord {
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;
    @SerializedName("elat")
    private double eLat;
    @SerializedName("elng")
    private double eLng;
    @SerializedName("segment_id")
    private int segmentId;
    @SerializedName("status")
    private SegmentStatus status;

    public int getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double geteLat() {
        return eLat;
    }

    public void seteLat(double eLat) {
        this.eLat = eLat;
    }

    public double geteLng() {
        return eLng;
    }

    public void seteLng(double eLng) {
        this.eLng = eLng;
    }

    public SegmentStatus getStatus() {
        return status;
    }

    public void setStatus(SegmentStatus status) {
        this.status = status;
    }

    public static class SegmentStatus {
        @SerializedName("color")
        public String color;
    }
}
