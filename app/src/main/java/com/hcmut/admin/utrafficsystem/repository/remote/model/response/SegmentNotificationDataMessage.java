package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SegmentNotificationDataMessage {
    @SerializedName("velocity")
    @Expose
    public int velocity;
    @SerializedName("include_user_report")
    @Expose
    public boolean includeUserReport;
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("segment")
    @Expose
    public int segment;
    @SerializedName("source")
    @Expose
    public String source;
    @SerializedName("color")
    @Expose
    public String color;
    @SerializedName("polyline")
    @Expose
    public Polyline polyline;
    @SerializedName("period_id")
    @Expose
    public String periodId;
    @SerializedName("__v")
    @Expose
    public int v;
    @SerializedName("createdAt")
    @Expose
    public String createdAt;
    @SerializedName("updatedAt")
    @Expose
    public String updatedAt;

    public static class Polyline {

        @SerializedName("coordinates")
        @Expose
        public List<List<Float>> coordinates = null;
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("type")
        @Expose
        public String type;
    }
}
