package com.hcmut.admin.utrafficsystem.business.trafficmodule;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.hcmut.admin.utrafficsystem.business.GoogleMapMemoryManager;
import com.hcmut.admin.utrafficsystem.business.trafficmodule.groundoverlay.StatusRenderImpl;
import com.hcmut.admin.utrafficsystem.business.trafficmodule.groundoverlay.StatusRender;
import com.hcmut.admin.utrafficsystem.business.trafficmodule.tileoverlay.TilerOverlayRenderModule;

import org.jetbrains.annotations.NotNull;

public class TrafficRenderModule {
    public static float MAX_ZOOM_LEVEL = 18f;

    /**
     * external view
     */
    private GoogleMap gmaps;
    private TilerOverlayRenderModule tilerOverlayRenderModule;
    private GoogleMapMemoryManager mapMemoryManager;
    private RefreshStatusHandler refreshStatusHandler;
    private StatusRender statusRender;

    public TrafficRenderModule(Context context, @NonNull GoogleMap map, @NotNull SupportMapFragment mapFragment) {
        this.gmaps = map;
        tilerOverlayRenderModule = new TilerOverlayRenderModule(gmaps, context);
        mapMemoryManager = new GoogleMapMemoryManager(mapFragment);
        refreshStatusHandler = new RefreshStatusHandler();
        statusRender = new StatusRenderImpl(gmaps, context);
        gmaps.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mapMemoryManager.onMapMove(gmaps.getCameraPosition().zoom);
                statusRender.onCameraMoving(gmaps);
            }
        });
        refreshStatusHandler.setOverlayRender(tilerOverlayRenderModule);
    }

    /**
     * API to turn on/off traffic status render
     * @param isEnable: true value to enable, false value to disable
     */
    public void setTrafficEnable(boolean isEnable) {
        tilerOverlayRenderModule.setTrafficEnable(isEnable);
        statusRender.setEnableTraffic(isEnable);
    }

    public void startStatusRenderTimer () {
        refreshStatusHandler.startStatusRenderTimer();
    }

    public void stopStatusRenderTimer () {
        refreshStatusHandler.stopStatusRenderTimer();
    }
}
