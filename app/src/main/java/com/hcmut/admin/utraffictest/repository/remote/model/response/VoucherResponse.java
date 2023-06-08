package com.hcmut.admin.utraffictest.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class VoucherResponse {
    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("value")
    private int value;
    @SerializedName("content")
    private String content;
    @SerializedName("startTime")
    private Date startTime;
    @SerializedName("endTime")
    private Date endTime;
    @SerializedName("partner_id")
    private String idPartner;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("image")
    private String image;
    public VoucherResponse(String id,String name,int value,String content,Date startTime,Date endTime,String idPartner,int quantity,String image){
        this.id=id;
        this.name=name;
        this.value=value;
        this.content=content;
        this.startTime=startTime;
        this.endTime= endTime;
        this.idPartner=idPartner;
        this.quantity=quantity;
        this.image=image;
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public String getContent() {
        return content;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getIdPartner() {
        return idPartner;
    }
    public int getIdCustomer() {
        return quantity;
    }
    public String getImage() {
        return image;
    }
}
