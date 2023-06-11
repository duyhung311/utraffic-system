package com.hcmut.admin.utrafficsystem.repository.remote.API;

import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.LayerRequest;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.LayerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APITurnByTurn {
    @Headers({"Content-Type: application/json", "apikey-bk: 6T3UEwanTaOGvSeT02oFgxembKCuezO53F00hXeDdaFKlZMBUVS6NEAQqdJbJBE4"})
    @POST("app/data-rqmxw/endpoint/fetchlayer")
    Call<BaseResponse<LayerResponse>> postGetLayer(@Body LayerRequest layerRequest);
}
