package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

public class PayMoMoResponse {
    @SerializedName("state")
    private int state;
    public PayMoMoResponse(int state) {
        this.state = state;
    }
    public int getState() {
        return state;
    }
}
