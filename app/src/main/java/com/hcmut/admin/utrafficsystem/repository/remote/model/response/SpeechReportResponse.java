package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

public class SpeechReportResponse {
    @SerializedName("code")
    private String code;
    @SerializedName("url")
    private String url;
    @SerializedName("job_id")
    private String job_id;

    public SpeechReportResponse(String code, String url) {
        this.code = code;
        this.url = url;
    };

    public String getUrl() {
        return this.url;
    };
    public String getCode() {
        return this.code;
    };

    public String getJobId() { return job_id; }
}
