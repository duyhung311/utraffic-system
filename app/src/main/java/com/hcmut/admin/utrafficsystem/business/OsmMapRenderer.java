package com.hcmut.admin.utrafficsystem.business;

import android.util.Log;

import com.hcmut.admin.utrafficsystem.constant.MobileConstants;
import com.hcmut.admin.utrafficsystem.model.osm.BoundingBox;
import com.hcmut.admin.utrafficsystem.repository.remote.API.APIService;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.repository.remote.model.BaseResponse;
import com.hcmut.admin.utrafficsystem.repository.remote.model.request.UpdateMapRequest;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.NavigationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OsmMapRenderer {


    private static APIService apiService;

    public OsmMapRenderer() {
        this.apiService = RetrofitClient.getApiService();
    }

    public NavigationResponse update(UpdateMapRequest updateMapRequest) {
        final NavigationResponse[] navigationResponse = {new NavigationResponse()};
        apiService.getNodeAndUpdateWayFromBound(updateMapRequest).enqueue(new Callback<BaseResponse<NavigationResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<NavigationResponse>> call, Response<BaseResponse<NavigationResponse>> response) {
                if (response.body() != null) {
                    navigationResponse[0] = response.body().getData();
                    Log.i(MobileConstants.DEBUG_TAGNAME, String.valueOf(navigationResponse[0].isChanging()));
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<NavigationResponse>> call, Throwable t) {

            }
        });
        return navigationResponse[0];
    }
}
