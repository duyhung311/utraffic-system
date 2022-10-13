package com.hcmut.admin.utrafficsystem.business.trafficmodule.groundoverlay;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.hcmut.admin.utrafficsystem.business.TileCoordinates;
import com.hcmut.admin.utrafficsystem.business.trafficmodule.tileoverlay.TrafficTileProvider;
import com.hcmut.admin.utrafficsystem.repository.remote.RetrofitClient;
import com.hcmut.admin.utrafficsystem.util.MyLatLngBoundsUtil;

public abstract class StatusRender {
    public static final int TILE_ZOOM_LEVEL = 15;
    private TileCoordinates lastCenterTile;
    private final int NEAR_LEVEL_TO_LOAD;

    public StatusRender () {
        NEAR_LEVEL_TO_LOAD = GroundOverlayMatrix.MATRIX_WIDTH / 2;
    }

    public void onCameraMoving(GoogleMap googleMap){
        int zoom = (int) googleMap.getCameraPosition().zoom;
        if (zoom > TrafficTileProvider.MAX_ZOOM_RENDER) {
            setEnableTraffic(true);
            final LatLng centerPoint = googleMap.getCameraPosition().target;
            RetrofitClient.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    TileCoordinates currentTile = getCenterRenderTile(centerPoint);
                    if (currentTile != null) {
                        handleCameraMoving(currentTile);
                    }
                }
            });
        } else {
            setEnableTraffic(false);
        }
    }

    private synchronized TileCoordinates getCenterRenderTile (LatLng centerPoint) {
        try {
            TileCoordinates currentTile = MyLatLngBoundsUtil
                    .getTileNumber(centerPoint.latitude, centerPoint.longitude, TILE_ZOOM_LEVEL);
            if (lastCenterTile == null) {
                lastCenterTile = currentTile;
                return currentTile;
            }
            if (currentTile.getNearLevel(lastCenterTile) > NEAR_LEVEL_TO_LOAD) {  // move to other tile
                lastCenterTile = currentTile;
                return currentTile;
            } else {
                Log.e("move", "is old tile");
            }
        } catch (TileCoordinates.TileCoordinatesNotValid tileCoordinatesNotValid) {
            tileCoordinatesNotValid.printStackTrace();
        }
        return null;
    }

    protected abstract void handleCameraMoving(TileCoordinates currentTile);

    public abstract void refreshRenderStatus(TileCoordinates centerTile);

    public abstract void setEnableTraffic(boolean isEnable);
}
