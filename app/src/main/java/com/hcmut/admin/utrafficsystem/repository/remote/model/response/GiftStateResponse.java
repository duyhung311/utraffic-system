package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

public class GiftStateResponse {
    @SerializedName("state")
    private int state;
    @SerializedName("point")
    private int point;
    public GiftStateResponse(int state,int point) {
        this.state = state;
        this.point = point;
    }
    public int getState() {
        return state;
    }
    public int getPoint() {
        return point;
    }
}
