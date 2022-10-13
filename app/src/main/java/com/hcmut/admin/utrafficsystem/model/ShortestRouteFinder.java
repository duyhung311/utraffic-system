package com.hcmut.admin.utrafficsystem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 10/5/2018.
 */

public class ShortestRouteFinder {
    private List<TrafficRoad> trafficRoadList;
    private List<RouteSegment> routeSegmentList;

    ShortestRouteFinder(List<TrafficRoad> trafficRoadList, List<RouteSegment> routeSegmentList) {
        this.trafficRoadList = new ArrayList<>(trafficRoadList);
        this.routeSegmentList = new ArrayList<>(routeSegmentList);
    }


}
