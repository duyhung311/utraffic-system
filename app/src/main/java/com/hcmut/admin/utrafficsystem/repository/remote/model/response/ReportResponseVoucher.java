package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ReportResponseVoucher {
    @SerializedName("date")
    private Date date;
    @SerializedName("pointIn")
    private int pointIn;
    @SerializedName("pointOut")
    private int pointOut;

    public ReportResponseVoucher(Date date,int pointIn,int pointOut){
        this.date=date;
        this.pointIn=pointIn;
        this.pointOut=pointOut;

    };

    public Date getDate() {
        return date;
    }

    public int getPointIn() {
        return pointIn;
    }

    public int getPointOut() {
        return pointOut;
    }
}
