package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppVersionResponse {
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("CHPlayUrl")
    @Expose
    public String cHPlayUrl;
    @SerializedName("appName")
    @Expose
    public String appName;
    @SerializedName("version")
    @Expose
    public String version;
    @SerializedName("versionCode")
    @Expose
    public int versionCode;
}
