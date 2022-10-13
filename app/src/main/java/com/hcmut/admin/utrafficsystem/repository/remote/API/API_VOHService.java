package com.hcmut.admin.utrafficsystem.repository.remote.API;

import com.hcmut.admin.utrafficsystem.repository.remote.model.request.FastReport;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface API_VOHService {

    @Headers("Content-Type: application/json")
    @POST("/api/fast-records")
    Call<BaseResponse<Object>> postFastRecord(@Body FastReport fastReport);
}
