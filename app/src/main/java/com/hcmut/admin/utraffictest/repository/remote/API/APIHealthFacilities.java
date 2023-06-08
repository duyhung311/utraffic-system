package com.hcmut.admin.utraffictest.repository.remote.API;

import com.hcmut.admin.utraffictest.model.DiagnosisInfo;
import com.hcmut.admin.utraffictest.model.HealthFacility;
import com.hcmut.admin.utraffictest.repository.remote.model.BaseResponse;
import com.hcmut.admin.utraffictest.repository.remote.model.request.healthFacilities.CommentRequest;
import com.hcmut.admin.utraffictest.repository.remote.model.request.healthFacilities.LikeCommentRequest;
import com.hcmut.admin.utraffictest.repository.remote.model.request.healthFacilities.RatingRequest;
import com.hcmut.admin.utraffictest.repository.remote.model.request.healthFacilities.StatusSendRequest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIHealthFacilities {

    @GET("/api/health-facilities/by-specialisation")
    Call<List<HealthFacility>> getHealthFacilitiesBySpecialisation(@Query("specialisation") String specialisation);

    @GET("/api/health-facilities/by-specialisationIDs")
    Call<List<HealthFacility>> getHealthFacilitiesBySpecialisationIDs(@Query("specialisationIds") List<Integer> specialisationIds);

    @GET("/api/health-facilities/by-partnerid")
    Call<List<HealthFacility>> getHealthFacilityByPartnerId(@Query("partner_id") String partnerId);

    @GET("/api/health-facilities/diagnosis")
    Call<List<DiagnosisInfo>> getHealthFacilitiesByDiagnosis(
            @Query("symptoms") List<String> symptoms,
            @Query("gender") String gender,
            @Query("year_of_birth") int year_of_birth);

    @GET("/api/health-facilities/all-symptoms")
    Call<List<String>> getAllSymptoms();

    @GET("/api/health-facilities/all-specialisations")
    Call<List<String>> getAllSpecialisation();

    @Multipart
    @POST("/api/register-health-facility/register")
    Call<ResponseBody> postHealthFacilityRegister(
            @Part("partner_id") RequestBody partner_id,
            @Part("name") RequestBody name,
            @Part("address") RequestBody address,
            @Part("work_time") RequestBody work_time,
            @Part("specialisation") List<RequestBody> specialisation,
            @Part("service") RequestBody service,
            @Part("phone_number") RequestBody phone_number,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part MultipartBody.Part license_image,
            @Part("state") RequestBody state);

    @PATCH("/api/health-facilities/update")
    Call<ResponseBody> updateHealthFacility(
            @Query("health_facility_id") String health_facility_id,
            @Query("name") String name,
            @Query("address") String address,
            @Query("work_time") String work_time,
            @Query("specialisation") List<String> specialisation,
            @Query("service") String service,
            @Query("phone_number") String phone_number,
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude);

    /*@PATCH("/api/health-facilities/add-comment")
    Call<ResponseBody> addComment(
            @Query("health_facility_id") String health_facility_id,
            @Query("user_id") String user_id,
            @Query("user_name") String user_name,
            @Query("content") String content);*/


    @POST("/api/health-facilities/comment")
    Call<BaseResponse<Object>> postUserCommentHealthFacilities(@Body CommentRequest commentRequest);

    @POST("/api/health-facilities/comment/like")
    Call<BaseResponse<Object>> postLikeCommentHealthFacilities(@Body LikeCommentRequest likeCommentRequest);

    @POST("/api/health-facilities/comment/status-send")
    Call<BaseResponse<Object>> postStatusSendCommentHealthFacilities(@Body StatusSendRequest statusSendRequest);

    @GET("/api/health-facilities/get-nearest-health-facilities")
    Call<List<HealthFacility>> getNearestHealthFacilities(@Query("longitude") Double longitude, @Query("latitude") Double latitude);

    @GET("api/health-facilities/search-health-facility")
    Call<List<HealthFacility>> getSearchHF(@Query("name") String name);

    @POST("api/health-facilities/rating-health-facility")
    Call<BaseResponse<Object>> ratingHF(@Body RatingRequest ratingRequest);

}