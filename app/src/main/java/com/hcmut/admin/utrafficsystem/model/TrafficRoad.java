package com.hcmut.admin.utrafficsystem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 10/5/2018.
 */

public class TrafficRoad {
    private String placeId;
    private int speedLimit;
    private List<TrafficData> trafficDataList;

    public TrafficRoad(TrafficData trafficData, int speedLimit) {
        this.placeId = trafficData.getPlaceId();
        this.speedLimit = speedLimit;
        trafficDataList = new ArrayList<>();
        trafficDataList.add(trafficData);
    }

    public String getPlaceId() {
        return placeId;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public List<TrafficData> getTrafficDataList() {
        return trafficDataList;

    }
}
