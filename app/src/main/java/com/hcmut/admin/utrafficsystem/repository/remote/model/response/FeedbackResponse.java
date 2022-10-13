package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedbackResponse {
    @SerializedName("response")
    @Expose
    public String response;
    @SerializedName("user")
    @Expose
    public String user;
    @SerializedName("message")
    @Expose
    public String message;
}
