package com.hcmut.admin.utraffictest.repository.local;

import android.content.Context;

import com.google.android.gms.maps.model.LatLngBounds;
import com.hcmut.admin.utraffictest.repository.RoomDatabaseService;
import com.hcmut.admin.utraffictest.repository.local.room.TrafficDatabase;
import com.hcmut.admin.utraffictest.repository.local.room.dao.StatusRenderDataDAO;
import com.hcmut.admin.utraffictest.repository.local.room.entity.StatusRenderDataEntity;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;

import java.util.List;

public class RoomDatabaseImpl implements RoomDatabaseService {
    private StatusRenderDataDAO statusRenderDataDAO;

    public RoomDatabaseImpl(Context context) {
        statusRenderDataDAO = TrafficDatabase.getInstance(context).getStatusRenderDataDAO();
    }

    @Override
    public void insertTrafficStatus(final List<StatusRenderDataEntity> datas) {
        RetrofitClient.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    statusRenderDataDAO.insertStatusDatas(datas);
                }
            }
        });
    }

    @Override
    public List<StatusRenderDataEntity> getTrafficStatus(LatLngBounds bounds, String streetType) {
        return statusRenderDataDAO.getStatusByBoundsAndStreetType(
                bounds.northeast.latitude,
                bounds.northeast.longitude,
                bounds.southwest.latitude,
                bounds.southwest.longitude,
                streetType);
    }

    @Override
    public List<StatusRenderDataEntity> getTrafficStatus(LatLngBounds bounds) {
        return statusRenderDataDAO.getStatusByBounds(
                bounds.northeast.latitude,
                bounds.northeast.longitude,
                bounds.southwest.latitude,
                bounds.southwest.longitude);
    }

    @Override
    public List<StatusRenderDataEntity> getTrafficStatus() {
        return statusRenderDataDAO.getStatus();
    }

    @Override
    public void deleteAll() {
        statusRenderDataDAO.deleteAllRecord();
    }
}
