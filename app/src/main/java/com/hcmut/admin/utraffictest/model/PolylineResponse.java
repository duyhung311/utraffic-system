package com.hcmut.admin.utraffictest.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PolylineResponse {
    @SerializedName("type")
    private String type;
    @SerializedName("coordinates")
    private ArrayList<ArrayList<Double>> coordinatesList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LatLng getStart() {
        return new LatLng(coordinatesList.get(0).get(1), coordinatesList.get(0).get(0));
    }

    public LatLng getEnd() {
        return new LatLng(coordinatesList.get(1).get(1), coordinatesList.get(1).get(0));
    }

    public ArrayList<ArrayList<Double>> getCoordinatesList() {
        return coordinatesList;
    }

    public void setCoordinatesList(ArrayList<ArrayList<Double>> coordinatesList) {
        this.coordinatesList = coordinatesList;
    }
}
