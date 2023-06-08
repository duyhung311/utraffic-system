package com.hcmut.admin.utraffictest.repository.remote.API;

import com.hcmut.admin.utraffictest.repository.remote.model.BaseResponse;
import com.hcmut.admin.utraffictest.repository.remote.model.request.LayerRequest;
import com.hcmut.admin.utraffictest.repository.remote.model.response.LayerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APITurnByTurn {
    @Headers({
            "Content-Type: application/json",
            "apikey-bk: 6474c0649a991fbf6d98c4e4"
    })
    //@POST("/api/segment/fetch-layers")
    @POST("/")
    Call<BaseResponse<LayerResponse>> postGetLayer(@Body LayerRequest layerRequest);
}
