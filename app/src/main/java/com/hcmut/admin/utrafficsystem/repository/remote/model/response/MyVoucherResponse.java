package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MyVoucherResponse {
    @SerializedName("_id")
    private String id;
    @SerializedName("voucher_id")
    private VoucherResponse voucherResponse;
    @SerializedName("createdAt")
    private Date createAt;
    @SerializedName("status")
    private int status;
    @SerializedName("code")
    private String code;
    @SerializedName("codeDeal")
    private String codeDeal;


    public MyVoucherResponse(String id,VoucherResponse voucherResponse,Date createAt,int status,String code,String codeDeal){
        this.id=id;
        this.voucherResponse=voucherResponse;
        this.createAt= createAt;
        this.status=status;
        this.code=code;
        this.codeDeal = codeDeal;
    };

    public String getId() {
        return id;
    }

    public VoucherResponse getVoucherResponse() {
        return voucherResponse;
    }

    public Date getDate() {
        return createAt;
    }
    public String getCode(){return code;}
    public int getStatus() {return status;}
    public String getCodeDeal(){return codeDeal;}


}
