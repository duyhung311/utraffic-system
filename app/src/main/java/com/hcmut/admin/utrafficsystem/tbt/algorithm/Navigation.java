package com.hcmut.admin.utrafficsystem.tbt.algorithm;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;
import android.util.Pair;

import com.hcmut.admin.utrafficsystem.repository.remote.model.response.Coord;

import java.util.HashMap;
import java.util.List;

@SuppressLint("NewApi")
public class Navigation {
    public static final HashMap<String, Double> bufferDistanceMap = new HashMap<>();

    static {
        bufferDistanceMap.put("motorway", 20.0);
        bufferDistanceMap.put("trunk", 15.0);
        bufferDistanceMap.put("primary", 12.5);
        bufferDistanceMap.put("secondary", 10.0);
        bufferDistanceMap.put("tertiary", 7.5);
        bufferDistanceMap.put("unclassified", 5.0);
        bufferDistanceMap.put("residential", 5.0);
        bufferDistanceMap.put("service", 4.0);
        bufferDistanceMap.put("motorway_link", 20.0);
        bufferDistanceMap.put("trunk_link", 15.0);
        bufferDistanceMap.put("primary_link", 12.5);
        bufferDistanceMap.put("secondary_link", 10.0);
        bufferDistanceMap.put("tertiary_link", 7.5);
        bufferDistanceMap.put("living_street", 4.0);
        bufferDistanceMap.put("pedestrian", 2.5);
        bufferDistanceMap.put("track", 2.5);
        bufferDistanceMap.put("bus_guideway", 4.0);
        bufferDistanceMap.put("escape", 5.0);
        bufferDistanceMap.put("raceway", 7.5);
        bufferDistanceMap.put("road", 5.0);
        bufferDistanceMap.put("footway", 1.0);
        bufferDistanceMap.put("bridleway", 1.0);
        bufferDistanceMap.put("steps", 1.0);
        bufferDistanceMap.put("path", 1.0);
        bufferDistanceMap.put("cycleway", 1.0);
        bufferDistanceMap.put("construction", 5.0);
        bufferDistanceMap.put("corridor", 0.5);
        bufferDistanceMap.put("elevator", 0.5);
        bufferDistanceMap.put("proposed", 5.0);
        bufferDistanceMap.put("prohibited", 0.0);
        bufferDistanceMap.put("crossing", 1.0);
        bufferDistanceMap.put("emergency_access_point", 0.5);
        bufferDistanceMap.put("give_way", 1.0);
        bufferDistanceMap.put("mini_roundabout", 2.5);
        bufferDistanceMap.put("motorway_junction", 20.0);
        bufferDistanceMap.put("passing_place", 1.0);
        bufferDistanceMap.put("rest_area", 2.5);
        bufferDistanceMap.put("speed_camera", 1.0);
        bufferDistanceMap.put("street_lamp", 0.5);
        bufferDistanceMap.put("services", 5.0);
        bufferDistanceMap.put("stop", 1.0);
        bufferDistanceMap.put("traffic_signals", 1.0);
        bufferDistanceMap.put("turning_circle", 2.5);
        bufferDistanceMap.put("toll_gantry", 5.0);
        bufferDistanceMap.put("turning_loop", 2.5);
        bufferDistanceMap.put("user_defined", 5.0);
    }

    public static Pair<Location, Float> isLocationNearPolyline(Location location, List<Coord> polyline) {
        Location rv = null;
        float totalDistance = 0;
        float currentDistance = 0;
        for (Coord coord : polyline) {
            Location pathStart = new Location("") {{
                setLatitude(coord.getLat());
                setLongitude(coord.getLng());
            }};
            Location pathEnd = new Location("") {{
                setLatitude(coord.geteLat());
                setLongitude(coord.geteLng());
            }};

            float pathDistance = pathStart.distanceTo(pathEnd);
            totalDistance += pathDistance;

            if (rv != null) {
                continue;
            }

            String type = coord.getStreet().type;
            if (type == null) {
                type = "unclassified";
            }
            double bufferDistanceInMeters = bufferDistanceMap.getOrDefault(type, 5.0);

            double[] closestPoint = calculateClosestPoint(location, pathStart, pathEnd);

            Location closestLocation = new Location("");
            closestLocation.setLatitude(closestPoint[0]);
            closestLocation.setLongitude(closestPoint[1]);
            closestLocation.setBearing(pathStart.bearingTo(pathEnd));

            boolean isNear = location.distanceTo(closestLocation) <= bufferDistanceInMeters;

            // Check if the location is near this coord of the polyline
            if (isNear) {
                rv = closestLocation;
                currentDistance = totalDistance + pathStart.distanceTo(closestLocation) - pathDistance;
            }
        }

        if (polyline.size() > 0) {
            Coord coord = polyline.get(0);
            Location pathStart = new Location("") {{
                setLatitude(coord.getLat());
                setLongitude(coord.getLng());
            }};
            Location pathEnd = new Location("") {{
                setLatitude(coord.geteLat());
                setLongitude(coord.geteLng());
            }};

            float pathDistance = pathStart.distanceTo(pathEnd);
            totalDistance -= pathDistance;
            currentDistance -= pathDistance;
        }
        float ratio = currentDistance / totalDistance;

        return new Pair<>(rv, ratio);
    }

    public static double[] calculateClosestPoint(Location point, Location lineStart, Location lineEnd) {
        double xDelta = lineEnd.getLongitude() - lineStart.getLongitude();
        double yDelta = lineEnd.getLatitude() - lineStart.getLatitude();

        if ((xDelta == 0) && (yDelta == 0)) {
            throw new IllegalArgumentException("Line start and end points are the same");
        }

        double u = ((point.getLongitude() - lineStart.getLongitude()) * xDelta + (point.getLatitude() - lineStart.getLatitude()) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

        final double[] closestPoint;
        if (u < 0) {
            closestPoint = new double[]{lineStart.getLatitude(), lineStart.getLongitude()};
        } else if (u > 1) {
            closestPoint = new double[]{lineEnd.getLatitude(), lineEnd.getLongitude()};
        } else {
            closestPoint = new double[]{lineStart.getLatitude() + u * yDelta, lineStart.getLongitude() + u * xDelta};
        }

        return closestPoint;
    }
}
