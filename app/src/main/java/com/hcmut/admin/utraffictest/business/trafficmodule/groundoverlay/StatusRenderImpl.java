package com.hcmut.admin.utraffictest.business.trafficmodule.groundoverlay;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.hcmut.admin.utraffictest.business.TileCoordinates;

public class StatusRenderImpl extends StatusRender {
    private GroundOverlayMatrix groundOverlayMatrix;

    public StatusRenderImpl(GoogleMap googleMap, Context context) {
        super();
        groundOverlayMatrix = new GroundOverlayMatrix(googleMap, context);
    }

    @Override
    protected void handleCameraMoving(TileCoordinates currentTile) {
        groundOverlayMatrix.renderMatrix(currentTile);
    }

    @Override
    public void refreshRenderStatus(TileCoordinates centerTile) {
        groundOverlayMatrix.refresh(centerTile);
    }

    @Override
    public void setEnableTraffic(boolean isEnable) {
        groundOverlayMatrix.setTrafficEnable(isEnable);
    }
}
