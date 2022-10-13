package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InfoVoucher {

    @SerializedName("name")
    private String name;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("point")
    private int point;
    @SerializedName("list_top")
    private List<VoucherResponse> list_top;
    @SerializedName("list_trend")
    private List<VoucherResponse>  list_trend;

    @SerializedName("slider")
    private List<SliderResponse> slider;
    public InfoVoucher(String name,String avatar,int point,List<SliderResponse> slider,List<VoucherResponse> list_top,List<VoucherResponse> list_trend) {
        this.avatar = avatar;
        this.name = name;
        this.point = point;
        this.slider = slider;
        this.list_top= list_top;
        this.list_trend= list_trend;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getPoint() {
        return point;
    }
    public List<SliderResponse> getSlider() {
        return slider;
    }
    public List<VoucherResponse> getListTrend() {
        return list_trend;
    }
    public List<VoucherResponse> getListTop() {
        return list_top;
    }

}
