package com.hcmut.admin.utraffictest.business.trafficmodule.groundoverlay;

import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.hcmut.admin.utraffictest.business.TileCoordinates;
import com.hcmut.admin.utraffictest.business.trafficmodule.TrafficBitmap;

import java.lang.ref.WeakReference;
import java.util.List;

public class TileRenderHandlerImpl extends TileRenderHandler {
    private WeakReference<GoogleMap> googleMapWeakReference;
    private TrafficBitmap trafficBitmap;

    public TileRenderHandlerImpl(GoogleMap googleMap) {
        super();
        googleMapWeakReference = new WeakReference<>(googleMap);
        trafficBitmap = new TrafficBitmap();
    }

    @Override
    public <T> Bitmap render(TileCoordinates tile, List<T> datas) {
        Bitmap bitmap = trafficBitmap.createTrafficBitmap(tile, datas, 4, 0.00018f);
        if (bitmap != null) {
            synchronized (TileRenderHandlerImpl.class) {
                GroundOverlayItem groundOverlayItem = GroundOverlayMatrix.getMatrixItem(tile);
                GoogleMap googleMap = googleMapWeakReference.get();
                if (groundOverlayItem != null && googleMap != null) {
                    groundOverlayItem.invalidateItself(bitmap, tile, googleMap);
                }
            }
        }
        return null;
    }

    @Override
    public void render(TileCoordinates tile, Bitmap bitmap) {

    }

    @Override
    public void clearRender() {

    }
}
