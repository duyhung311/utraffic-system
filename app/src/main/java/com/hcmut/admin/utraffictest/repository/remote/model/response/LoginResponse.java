package com.hcmut.admin.utraffictest.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("access_token")
    private String accessToken;
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
    @SerializedName("evaluation_score")
    private int evaluation_score;
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

    public int getEvaluation_score() {
        return evaluation_score;
    }

    public String getNotiToken() {
        return notiToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
    public String getRole() {
        return role;
    }
}
