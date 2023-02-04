package com.hcmut.admin.utrafficsystem.repository.remote.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class SpeechReportRequest {
    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("content")
    @Expose
    private Content content;

    @SerializedName("input")
    @Expose
    private String input;

    @SerializedName("output")
    @Expose
    private String output;

    @SerializedName("audioFile")
    @Expose
    private File audioFile;

    public SpeechReportRequest(String url) {
        this.url = url;
    }

    public SpeechReportRequest(Content content, String input, String output) {
        this.content = content;
        this.input = input;
        this.output = output;
    }

    public SpeechReportRequest(File audioFile) {
        this.audioFile = audioFile;
    }

    public String getUrl() {
        return this.url;
    }

}
