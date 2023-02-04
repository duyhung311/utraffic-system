package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

public class SpeechReportResponse {
    @SerializedName("code")
    private String code;
    @SerializedName("url")
    private String url;
    @SerializedName("job_id")
    private String job_id;
    @SerializedName("status")
    private String status;
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

    public void setCode(String code) {
        this.code = code;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
