package com.hcmut.admin.utrafficsystem.repository.remote.API;

import com.hcmut.admin.utrafficsystem.model.User;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.RatingBody;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.ReportRequest;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.DealResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.DirectRespose;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.GiftResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.GiftStateResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.InfoPaymentResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.InfoVoucher;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.FeedbackRequest;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.AppVersionResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.FeedbackResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.LoginResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.MyVoucherResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.NearSegmentResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.PatchNotiResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.PayMoMoResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.PostRatingResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.ReportResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.ReportResponseVoucher;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.SpeechReportResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.TrafficReportResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.TrafficStatusResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.UserResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.StatusResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.StatusRenderData;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.VoucherResponse;

import org.bson.types.ObjectId;

import okhttp3.RequestBody;

import java.util.List;

import javax.annotation.Nullable;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    /**
     *
     */
    @GET("/api/app-version/{id}")
    Call<BaseResponse<AppVersionResponse>> getCurrentAppVersionInfo(@Path("id") String id);

    /**
     *
     */
    @GET("/api/traffic-status/get-status")
    Call<StatusResponse<List<StatusRenderData>>> getTrafficStatus(@Query("lat") Double lat, @Query("lng") Double lng, @Query("zoom") Double zoom);

    /**
     * get traffic status by radius in meters
     */
    @GET("/api/traffic-status/get-status")
    Call<StatusResponse<List<StatusRenderData>>> getTrafficStatus(@Query("lat") Double lat, @Query("lng") Double lng, @Query("radius_in_meter") int radiusInMeters, @Query("level") int level);

    @GET("/api/traffic-status/get-status-v2")
    Call<StatusResponse<List<StatusRenderData>>> getTrafficStatus(@Query("NElat") Double NElat, @Query("NElng") Double NElng, @Query("WSlat") Double WSlat, @Query("WSlng") Double WSlng, @Query("level") int level);
    /**
     * get traffic status by radius in meters
     */
    @GET("/api/traffic-status/get-status?include_user_report=true")
    Call<StatusResponse<List<StatusRenderData>>> getTrafficStatusIncludeUserReport(
            @Query("lat") Double lat,
            @Query("lng") Double lng,
            @Query("radius_in_meter") int radiusInMeters);

    @GET("api/traffic-status/get-status")
    Call<BaseResponse<List<TrafficStatusResponse>>> getVelocity(@Query("time") long date,
                                                                @Query("segmentId") int segmentId);

    @POST("api/feedback/create")
    Call<BaseResponse<FeedbackResponse>> postUserFeedback(@Header("Authorization") String Authorization,
                                                          @Body FeedbackRequest feedbackRequest);

    @POST("api/report/segment/here")
    Call<BaseResponse<ReportResponse>> postTrafficReport(@Header("Authorization") String Authorization,
                                                         @Body ReportRequest reportRequest);

    @POST("api/report/segment/here")
    Call<BaseResponse<ReportResponse>> postGPSTrafficReport(@Header("Authorization") String Authorization,
                                                            @Body ReportRequest reportRequest);

    @Headers("Content-Type: application/json")
    @POST("api/evaluation/add")
    Call<BaseResponse<PostRatingResponse>> postRating(@Header("Authorization") String Authorization,
                                                      @Body RatingBody ratingBody);

    @GET("api/user/get-user-info")
    Call<BaseResponse<UserResponse>> getUserInfo(@Header("Authorization") String Authorization);

    @POST("api/user/update-user-info")
    @FormUrlEncoded
    Call<BaseResponse<UserResponse>> updateUserInfo(@Header("Authorization") String Authorization,
                                                    @Field("name") String name,
                                                    @Field("email") String email,
                                                    @Field("avatar") String avatar,
                                                    @Field("phone") String phone);

    @POST("api/auth/login-with-facebook")
    @FormUrlEncoded
    Call<BaseResponse<LoginResponse>> loginWithFacebook(@Field("facebook_id") String facebookId,
                                                        @Field("facebook_token") String facebookToken);

    @POST("api/auth/login-with-google")
    @FormUrlEncoded
    Call<BaseResponse<LoginResponse>> loginWithGoogle(@Field("google_id") String googleId,
                                                      @Field("google_token") String googleToken);

    @POST("api/auth/login")
    @FormUrlEncoded
    Call<BaseResponse<LoginResponse>> login(@Field("username") String username,
                                            @Field("password") String password);

    @POST("api/auth/register")
    @FormUrlEncoded
    Call<BaseResponse<LoginResponse>> register(@Field("username") String username,
                                               @Field("password") String password,
                                               @Field("name") String name,
                                               @Field("email") String email,
                                               @Field("phone") String phone);

    @GET("api/segment/find-near")
    Call<BaseResponse<List<NearSegmentResponse>>> getNearSegment(@Query("lat") double lat,
                                                                 @Query("lng") double lng);

    @GET("api/report/segment/current-reports")
    Call<BaseResponse<List<TrafficReportResponse>>> getCurrentTrafficReport(@Query("lat") double lat,
                                                                            @Query("lng") double lng);

    @GET("api/report/segment/report-detail")
    Call<BaseResponse<ReportResponse>> getDetailTrafficReport(@Query("id") String id);

    @GET("api/report/segment/reports")
    Call<BaseResponse<List<ReportResponse>>> getReportOfTrafficStatus(@Query("time") Long time,
                                                                      @Query("segmentId") int segmentId);

    @FormUrlEncoded
    @POST("api/notification/update-current-location")
    Call<BaseResponse<PatchNotiResponse>> updateCurrentLocation(@Header("Authorization") String Authorization,
                                                                @Field("token") String notiToken,
                                                                @Field("lat") Double currentLat,
                                                                @Field("lng") Double currentLng,
                                                                @Field("active") String active,
                                                                @Nullable @Field("path_id") String pathId);

    @GET("api/segment/direct")
    Call<BaseResponse<List<DirectRespose>>> getFindDirect(@Query("slat") double slat,
                                                          @Query("slng") double slng,
                                                          @Query("elat") double elat,
                                                          @Query("elng") double elng,
                                                          @Query("type") String type);

    @Multipart
    @POST("api/file/upload")
    Call<BaseResponse<String>> uploadFile(@Part MultipartBody.Part file);


    @GET("api/voucher/gettopvoucher")
    Call<BaseResponse<List<VoucherResponse>>> getTopVoucher();

    @GET("api/voucher/gettrendvoucher")
    Call<BaseResponse<List<VoucherResponse>>> getTrendVoucher();

    @GET("api/voucher/getalltopvoucher")
    Call<BaseResponse<List<VoucherResponse>>> getAllTopVoucher();

    @GET("api/voucher/getalltrendvoucher")
    Call<BaseResponse<List<VoucherResponse>>> getAllTrendVoucher();

    @GET("api/voucher/getdetailvoucher")
    Call<BaseResponse<VoucherResponse>> getDetailVoucher(@Query("_id") String id);

    @GET("api/voucher/getinfopaymentvoucher")
    Call<BaseResponse<InfoPaymentResponse>> getInfoPaymentVoucher(@Header("Authorization") String Authorization,@Query("_id") String id);

    @POST("api/voucher/confirmpaymentvoucher")
    @FormUrlEncoded
    Call<BaseResponse<VoucherResponse>> paymentVoucher(@Header("Authorization") String Authorization,
                                                         @Field("id") String id);

    @GET("api/voucher/getsearchvoucher")
    Call<BaseResponse<List<VoucherResponse>>> getSearchVoucher(@Header("Authorization") String Authorization,@Query("word") String word);

    @GET("api/voucher/getmyvoucher")
    Call<BaseResponse<List<MyVoucherResponse>>> getMyVoucher(@Header("Authorization") String Authorization);

    @GET("api/voucher/getdetailmyvoucher")
    Call<BaseResponse<MyVoucherResponse>> getDetailMyVoucher(@Header("Authorization") String Authorization,@Query("id") String id);

    @POST("api/voucher/finduser")
    @FormUrlEncoded
    Call<BaseResponse<UserResponse>> findUser(@Header("Authorization") String Authorization, @Field("word") String word);

    @GET("api/voucher/getmessageauthentication")
    Call<BaseResponse> getMessageAuthentication(@Header("Authorization") String Authorization);

    @POST("api/voucher/confirmauthentication")
    @FormUrlEncoded
    Call<BaseResponse> confirmAuthentication(@Header("Authorization") String Authorization, @Field("point") int point,@Field("message") String message,@Field("authen") String authen,@Field("receive") String receive);

    @GET("api/voucher/getdealvoucher")
    Call<BaseResponse<List<DealResponse>>> getDealVoucher(@Header("Authorization") String Authorization);

    @GET("api/voucher/getreportvoucher")
    Call<BaseResponse<List<ReportResponseVoucher>>> getReportVoucher(@Header("Authorization") String Authorization);

    @GET("api/voucher/getinfovoucher")
    Call<BaseResponse<InfoVoucher>> getInfoVoucher(@Header("Authorization") String Authorization);

    @POST("api/voucher/confirmqrcode")
    @FormUrlEncoded
    Call<BaseResponse> confirmQRCode(@Header("Authorization") String Authorization,@Field("code") String code);

    @POST("api/paymentrequest")
    @FormUrlEncoded
    Call<BaseResponse<PayMoMoResponse>> paymentRequest(@Header("Authorization") String Authorization, @Field("token") String token, @Field("phone")String phone, @Field("order")String order, @Field("amount")int amount, @Field("point")int point);

    @GET("api/gift/getallgift")
    Call<BaseResponse<List<GiftResponse>>> getAllGift(@Header("Authorization") String Authorization);

    @POST("api/gift/checkgift")
    @FormUrlEncoded
    Call<BaseResponse<GiftStateResponse>> checkGift(@Header("Authorization") String Authorization, @Field("id") String id);

    @Multipart
    @POST("/api/report/speech-report")
    Call<SpeechReportResponse> triggerServer(@Part("segments") List<RequestBody> segments,
                                             @Part("speech_record_id") RequestBody speechRecordId,
                                             @Part MultipartBody.Part record);

    @Multipart
    @POST("/api/report/speech-report/mobile")
    Call<SpeechReportResponse> callServerForEnhanceRecord(@Part("segments") List<Integer> segments,
                                                          @Part("speech_record_id") String speechRecordId,
                                                          @Part("type") String type, // circle | rectangle | line
                                                          @Part("coordinates")Double[][] coordinates,
                                                          @Part("active_time") Integer activeTime, // active time in isecond
                                                          @Part("radius") Double radius, // radius = [0;3000)
                                                          @Part("option") int option, // 0 or 1
                                                          //@Part MultipartBody.Part record,
                                                          @Part("file") String file, // content of file is encoded to string b4 sending
                                                          @Part("user") User user); //curent logged in userId
}
