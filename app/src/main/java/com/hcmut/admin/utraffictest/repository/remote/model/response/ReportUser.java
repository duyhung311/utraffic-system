package com.hcmut.admin.utraffictest.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

public class ReportUser {
    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("evaluation_score")
    private float reputation;

    public ReportUser() {}

    public ReportUser(String id, String name, float reputation) {
        this.id = id;
        this.name = name;
        this.reputation = reputation;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public float getReputation() {
        return reputation;
    }
}
