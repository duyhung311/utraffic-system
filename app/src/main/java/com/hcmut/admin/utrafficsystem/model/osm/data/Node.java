package com.hcmut.admin.utrafficsystem.model.osm.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class Node {
    private float lat;
    private float lon;
    private HashMap<String, String> tags;

    public HashMap<String, String> getTags() {
        return tags;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public Node(float lon, float lat) {
        this.lon = lon;
        this.lat = lat;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Node && ((Node) obj).lat == lat && ((Node) obj).lon == lon;
    }

    @NonNull
    @Override
    public String toString() {
        return "Node{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
