package com.hcmut.admin.utrafficsystem.repository.remote.API;

import com.hcmut.admin.utrafficsystem.model.Atm;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.FastReport;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.SpeechReportRequest;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.SpeechReportResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

public interface APISpeechReport {

   @Headers({"Content-Type: application/json",
           "x-api-key: xFCkf0XXsADpSAAPvfAbfYxA2XQacHlp"})
    @POST("/media/input")
    Call<SpeechReportResponse> createDolbyInputBucket(@Body SpeechReportRequest speechReportRequest);

    @PUT
    Call<SpeechReportResponse> uploadAudioFileToDolby(@Header("Content-Type") String contentType,
                                                                           @Url String url,
                                                                           @Body SpeechReportRequest speechReportRequest);
    @Headers({"Content-Type: application/json",
            "x-api-key: xFCkf0XXsADpSAAPvfAbfYxA2XQacHlp"})
    @POST("/media/enhance")
    Call<SpeechReportResponse> initEnhanceAudioDolby(@Body SpeechReportRequest speechReportRequest);

}
