package com.hcmut.admin.utraffictest.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrafficReportResponse {

    @SerializedName("_id")
    private String id;
    @SerializedName("velocity")
    private int velocity;
    @SerializedName("description")
    private String description;
    @SerializedName("images")
    private ArrayList<String> images;
    @SerializedName("causeId")
    private int causeId;
    @SerializedName("userId")
    private int userId;
    @SerializedName("user")
    private ReportUser user;
    @SerializedName("segmentIds")
    private List<Integer> segmentIds;
    @SerializedName("center_point")
    private CenterPointResponse centerPoint;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public int getCauseId() {
        return causeId;
    }

    public void setCauseId(int causeId) {
        this.causeId = causeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Integer> getSegmentIds() {
        return segmentIds;
    }

    public void setSegmentIds(List<Integer> segmentIds) {
        this.segmentIds = segmentIds;
    }

    public CenterPointResponse getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(CenterPointResponse centerPoint) {
        this.centerPoint = centerPoint;
    }

    public ReportUser getUser() {
        return user;
    }
}
