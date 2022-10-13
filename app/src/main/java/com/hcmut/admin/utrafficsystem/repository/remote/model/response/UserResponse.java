package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("email")
    private String email;
    @SerializedName("notiToken")
    private String notiToken;
    @SerializedName("phone")
    private String phoneNumber;
    @SerializedName("reputation")
    private String reputation;
    @SerializedName("point")
    private int point;
    @SerializedName("role")
    private String role;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getReputation() {
        return reputation;
    }

    public String getNotiToken() {
        return notiToken;
    }

    public int getPoint() {
        return point;
    }
    public String getRole() {
        return role;
    }
}
