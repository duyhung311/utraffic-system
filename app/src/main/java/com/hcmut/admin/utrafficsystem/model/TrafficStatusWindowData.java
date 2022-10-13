package com.hcmut.admin.utrafficsystem.model;

/**
 * Created by Admin on 12/3/2018.
 */

public class TrafficStatusWindowData {
    private int segmentSpeed;
    private int segmentStatus;
    private String roadName;
    private int roadSpeed;

    public int getSegmentSpeed() {
        return segmentSpeed;
    }

    public int getSegmentStatus() {
        return segmentStatus;
    }

    public String getRoadName() {
        return roadName;
    }

    public int getRoadSpeed() {
        return roadSpeed;
    }

    public void setSegmentSpeed(int segmentSpeed) {
        this.segmentSpeed = segmentSpeed;
    }

    public void setSegmentStatus(int segmentStatus) {
        this.segmentStatus = segmentStatus;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public void setRoadSpeed(int roadSpeed) {
        this.roadSpeed = roadSpeed;
    }
}
