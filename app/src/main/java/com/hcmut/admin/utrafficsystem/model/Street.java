package com.hcmut.admin.utrafficsystem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 11/15/2018.
 */

public class Street {
    private String id;
    private String name;
    private long maxSpeed;
    private List<TrafficSegment> trafficSegmentsList;

    public Street(String id, String name, long maxSpeed, List<TrafficSegment> trafficSegmentsList) {
        this.id = id;
        this.name = name;
        this.maxSpeed = maxSpeed;
        this.trafficSegmentsList = new ArrayList<>(trafficSegmentsList);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getMaxSpeed() {
        return maxSpeed;
    }

    public List<TrafficSegment> getTrafficSegmentsList() {
        return trafficSegmentsList;
    }
}



