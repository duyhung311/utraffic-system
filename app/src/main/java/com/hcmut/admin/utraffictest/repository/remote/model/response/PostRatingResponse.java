package com.hcmut.admin.utraffictest.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

public class PostRatingResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("score")
    private float score;
    @SerializedName("userId")
    private int userId;
    @SerializedName("reportId")
    private int reportId;
}
