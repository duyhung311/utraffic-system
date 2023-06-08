package com.hcmut.admin.utraffictest.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CenterPointResponse {
    @SerializedName("type")
    private String type;
    @SerializedName("coordinates")
    private List<Double> coordinatesList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double> getCoordinatesList() {
        return coordinatesList;
    }

    public void setCoordinatesList(List<Double> coordinatesList) {
        this.coordinatesList = coordinatesList;
    }
}
