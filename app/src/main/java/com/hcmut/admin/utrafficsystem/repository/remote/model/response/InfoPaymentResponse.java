package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

public class InfoPaymentResponse {
    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("value")
    private int value;
    @SerializedName("beforePoint")
    private int beforePoint;
    @SerializedName("payPoint")
    private int payPoint;
    @SerializedName("afterPoint")
    private int afterPoint;
    @SerializedName("image")
    private String image;

//    public InfoPaymentResponse(String id,String name,int value,String content,Date startTime,Date endTime,String idPartner,int quantity){
//        this.id=id;
//        this.name=name;
//        this.value=value;
//        this.content=content;
//        this.startTime=startTime;
//        this.endTime= endTime;
//        this.idPartner=idPartner;
//        this.quantity=quantity;
//    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public int getBeforePoint() {
        return beforePoint;
    }

    public int getPayPoint() {
        return payPoint;
    }

    public int getAfterPoint() {
        return afterPoint;
    }
    public String getImage() {
        return image;
    }

}
