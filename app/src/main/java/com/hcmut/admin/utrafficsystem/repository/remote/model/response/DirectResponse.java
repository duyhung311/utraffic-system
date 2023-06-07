package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectResponse {
    @SerializedName("_id")
    private String pathId;
    @SerializedName("distance")
    private int distance;
    @SerializedName("time")
    private double time;
    @SerializedName("coords")
    private List<Coord> coords;

    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public List<Coord> getCoords() {
        return coords;
    }

    public void setCoords(List<Coord> coords) {
        this.coords = coords;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
