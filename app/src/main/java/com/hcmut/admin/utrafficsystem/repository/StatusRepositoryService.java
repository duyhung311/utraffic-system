package com.hcmut.admin.utrafficsystem.repository;

import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.utrafficsystem.business.UserLocation;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.StatusRenderData;

import java.util.List;

public interface StatusRepositoryService {
    List<StatusRenderData> loadStatusRenderData(UserLocation userLocation, double zoom);
    List<StatusRenderData> loadStatusRenderData(UserLocation userLocation, int radiusInMeters, int level);
    List<StatusRenderData> loadStatusRenderData(LatLngBounds bound, int level);
}
