package com.hcmut.admin.utraffictest.business.trafficmodule.tileoverlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.hcmut.admin.utraffictest.business.TileCoordinates;
import com.hcmut.admin.utraffictest.business.trafficmodule.TrafficDataLoader;
import com.hcmut.admin.utraffictest.business.trafficmodule.TrafficBitmap;
import com.hcmut.admin.utraffictest.repository.local.room.entity.StatusRenderDataEntity;
import com.hcmut.admin.utraffictest.util.MapUtil;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class TrafficTileProvider implements TileProvider {
    public static final int MAX_ZOOM_RENDER = 16;

    private TrafficBitmap trafficBitmap;
    private TrafficDataLoader trafficDataLoader;

    public TrafficTileProvider(Context context) {
        trafficBitmap = new TrafficBitmap();
        trafficDataLoader = TrafficDataLoader.getInstance(context);
    }

    @Override
    public Tile getTile(int x, int y, int z) {
        if (z < 8 || z > MAX_ZOOM_RENDER) return NO_TILE;
        try {
            TileCoordinates renderTile = TileCoordinates.getTileCoordinates(x, y, z);
            if (MapUtil.IsOutLatLngBounds(renderTile, TileCoordinates.getHCMCityLatLngBounds())) {
                Log.e("TILE", "NO TILE");
                return NO_TILE;
            }
            return generateTileFromRemoteData(renderTile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Tile generateTileFromRemoteData(TileCoordinates renderTile) {
        int scale = getScaleByZoom(renderTile);
        List<StatusRenderDataEntity> dataEntityList;
        if (renderTile.z > 12) {
            dataEntityList = trafficDataLoader.loadTrafficDataFromServer(renderTile);
        } else {
            dataEntityList = trafficDataLoader.loadTrafficDataForHCMCityFromServer(renderTile);
        }
        Bitmap bitmap = trafficBitmap.createTrafficBitmap(renderTile, dataEntityList, scale, null);
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            bitmap.recycle();
            return new Tile(TrafficBitmap.mTileSize * scale, TrafficBitmap.mTileSize * scale, byteArray);
        }
        return null;
    }

    private Tile generateTile(TileCoordinates renderTile) {
        int scale = getScaleByZoom(renderTile);
        List<StatusRenderDataEntity> dataList = trafficDataLoader.loadDataFromLocal(renderTile);
        Bitmap bitmap = trafficBitmap.createTrafficBitmap(renderTile, dataList, scale, null);
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            bitmap.recycle();
            return new Tile(TrafficBitmap.mTileSize * scale, TrafficBitmap.mTileSize * scale, byteArray);
        }
        return null;
    }

    private int getScaleByZoom(TileCoordinates tile) {
        switch (tile.z) {
            case 15:
                return 2;
            default:
                return 2;
        }
    }
}
