package com.hcmut.admin.utrafficsystem.repository.remote.model.response;

import com.google.gson.annotations.SerializedName;

public class SliderResponse {
    @SerializedName("image")
    private String image;
    @SerializedName("name")
    private String name;
    @SerializedName("content")
    private String content;
    @SerializedName("type")
    private String type;
    @SerializedName("locate")
    private String locate;
    public SliderResponse(String image,String name, String content, String type, String locate){
        this.image=image;
        this.name=name;
        this.content=content;
        this.locate=locate;
        this.type= type;


    };

    public String getImage() {
        return image;
    }
    public String getContent() {
        return content;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public String getLocate() {
        return locate;
    }
}
