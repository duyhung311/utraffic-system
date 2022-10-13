package com.hcmut.admin.utrafficsystem.repository.remote.API;

import com.hcmut.admin.utrafficsystem.model.Atm;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.healthFacilities.RatingRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIAtm {
    @GET("/api/atm/get-atm-list")
    Call<List<Atm>> getAtmList();

    @GET("/api/atm/get-top-number-atm-list")
    Call<List<Atm>> getTopNumberAtmList();

    @GET("/api/atm/get-atm-near-your-location")
    Call<List<Atm>> getAtmNearYourLocation(@Query("longitude") Double longitude, @Query("latitude") Double latitude);

    @GET("api/atm/search-atm")
    Call<List<Atm>> getSearchAtm(@Query("name") String name,@Query("longitude") Double longitude, @Query("latitude") Double latitude);

    @POST("api/atm/rating-atm")
    Call<BaseResponse<Object>> ratingAtm(@Body RatingRequest ratingRequest);
}
