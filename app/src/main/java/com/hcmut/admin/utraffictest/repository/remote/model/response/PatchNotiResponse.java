package com.hcmut.admin.utraffictest.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

public class PatchNotiResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("createdDate")
    private long createdDate;
    @SerializedName("reputation")
    private float reputation;
    @SerializedName("token")
    private String notiToken;
    @SerializedName("active")
    private String active;
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public float getReputation() {
        return reputation;
    }

    public String getNotiToken() {
        return notiToken;
    }
}
