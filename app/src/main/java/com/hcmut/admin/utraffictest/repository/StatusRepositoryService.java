package com.hcmut.admin.utraffictest.repository;

import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.utraffictest.business.UserLocation;
import com.hcmut.admin.utraffictest.repository.remote.model.response.StatusRenderData;

import java.util.List;

public interface StatusRepositoryService {
    List<StatusRenderData> loadStatusRenderData(UserLocation userLocation, double zoom);
    List<StatusRenderData> loadStatusRenderData(UserLocation userLocation, int radiusInMeters, int level);
    List<StatusRenderData> loadStatusRenderData(LatLngBounds bound, int level);
}
