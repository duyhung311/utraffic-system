package com.hcmut.admin.utrafficsystem.repository.remote.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.List;

public class SpeechReportBody {

    @SerializedName("record")
    @Expose
    private File record;

    @SerializedName("segments")
    @Expose
    private List<Integer> segments;

    @SerializedName("speech_record_id")
    @Expose
    private String speechRecordId;

    public File getRecord() {
        return record;
    }

    public void setRecord(File record) {
        this.record = record;
    }

    public List<Integer> getSegments() {
        return segments;
    }

    public void setSegments(List<Integer> segments) {
        this.segments = segments;
    }

    public String getSpeechRecordId() {
        return speechRecordId;
    }

    public void setSpeechRecordId(String speechRecordId) {
        this.speechRecordId = speechRecordId;
    }
}
