package com.hcmut.admin.utrafficsystem.repository.local.room.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.hcmut.admin.utrafficsystem.repository.remote.model.response.StatusRenderData;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "status_render_data")
public class StatusRenderDataEntity {
    @PrimaryKey
    @ColumnInfo(name = "segment")
    public Long segment;

    @ColumnInfo(name = "color")
    public String color;

    @ColumnInfo(name = "startLat")
    public double startLat;

    @ColumnInfo(name = "startLng")
    public double startLng;

    @ColumnInfo(name = "endLat")
    public double endLat;

    @ColumnInfo(name = "endLng")
    public double endLng;

    @ColumnInfo(name = "streetType")
    public String streetType;

    public LatLng getStartLatlng () {
        return new LatLng(startLat, startLng);
    }

    public LatLng getEndLatlng () {
        return new LatLng(endLat, endLng);
    }

    public StatusRenderDataEntity() {

    }

    public StatusRenderDataEntity(StatusRenderData data) {
        segment = data.getSegment();
        color = data.getColor();
        try {
            List<List<Double>> coord = data.getPolyline().getCoordinates();
            startLat = coord.get(0).get(1);
            startLng = coord.get(0).get(0);
            endLat = coord.get(1).get(1);
            endLng = coord.get(1).get(0);
        } catch (Exception e) {

        }
    }

    public static List<StatusRenderDataEntity> parseStatusRenderDataEntity(List<StatusRenderData> datas) {
        List<StatusRenderDataEntity> entities = new ArrayList<>();
        StatusRenderDataEntity entity;
        List<List<Double>> coord;
        for (StatusRenderData data : datas) {
            try {
                coord = data.getPolyline().getCoordinates();
                entity = new StatusRenderDataEntity();
                entity.segment = data.getSegment();
                entity.color = data.getColor();
                entity.startLat = coord.get(0).get(1);
                entity.startLng = coord.get(0).get(0);
                entity.endLat = coord.get(1).get(1);
                entity.endLng = coord.get(1).get(0);
                entity.streetType = data.getStreet().type;
                entities.add(entity);
            } catch (Exception e) {
            }
        }
        return entities;
    }

    @NonNull
    @Override
    public String toString() {
        return color + ", startLat " + startLat + ", startLng " + startLng;
    }
}
