package com.hcmut.admin.utraffictest.repository;

import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.utraffictest.business.UserLocation;
import com.hcmut.admin.utraffictest.repository.remote.API.APIService;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.repository.remote.model.StatusResponse;
import com.hcmut.admin.utraffictest.repository.remote.model.response.StatusRenderData;

import java.util.List;

import retrofit2.Response;

public class StatusRemoteRepository implements StatusRepositoryService {
    private APIService apiService;

    public StatusRemoteRepository() {
        apiService = RetrofitClient.getApiService();
    }

    @Override
    public List<StatusRenderData> loadStatusRenderData(UserLocation userLocation, double zoom) {
        if (userLocation != null) {
            try {
                Response<StatusResponse<List<StatusRenderData>>> response = apiService
                        .getTrafficStatus(userLocation.getLatitude(), userLocation.getLongitude(), zoom)
                        .execute();
                return response.body().getData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public List<StatusRenderData> loadStatusRenderData(UserLocation userLocation, int radiusInMeters, int level) {
        if (userLocation != null) {
            try {
                Response<StatusResponse<List<StatusRenderData>>> response = apiService
                        .getTrafficStatus(userLocation.getLatitude(), userLocation.getLongitude(), radiusInMeters, level)
                        .execute();
                return response.body().getData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public List<StatusRenderData> loadStatusRenderData(LatLngBounds bound, int level) {
        if (bound != null) {
            try {
                Response<StatusResponse<List<StatusRenderData>>> response = apiService
                        .getTrafficStatus(
                                bound.northeast.latitude,
                                bound.northeast.longitude,
                                bound.southwest.latitude,
                                bound.southwest.longitude,
                                level
                        ).execute();
                return response.body().getData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
