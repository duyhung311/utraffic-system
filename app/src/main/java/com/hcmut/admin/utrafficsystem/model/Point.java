package com.hcmut.admin.utrafficsystem.model;

import com.google.android.gms.maps.model.LatLng;

public class Point {
    private String reportId;
    private LatLng position;
    private float velocity;

    public Point(String reportId, LatLng position, float velocity) {
        this.reportId = reportId;
        this.position = position;
        this.velocity = velocity;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }
}
