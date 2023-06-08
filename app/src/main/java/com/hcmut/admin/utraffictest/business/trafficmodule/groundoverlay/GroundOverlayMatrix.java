package com.hcmut.admin.utraffictest.business.trafficmodule.groundoverlay;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.hcmut.admin.utraffictest.business.TileCoordinates;
import com.hcmut.admin.utraffictest.business.trafficmodule.TrafficDataLoader;
import com.hcmut.admin.utraffictest.repository.local.room.entity.StatusRenderDataEntity;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GroundOverlayMatrix {
    public static final int MATRIX_WIDTH = 3;

    private TileRenderHandler tileRenderHandler;
    private TrafficDataLoader trafficDataLoader;
    private static HashMap<TileCoordinates, GroundOverlayItem> matrix = new HashMap<>();

    public static GroundOverlayItem getMatrixItem(TileCoordinates tile) {
        return matrix.get(tile);
    }

    public GroundOverlayMatrix(GoogleMap googleMap, Context context) {
        tileRenderHandler = new TileRenderHandlerImpl(googleMap);
        trafficDataLoader = TrafficDataLoader.getInstance(context);
    }

    public synchronized void renderMatrix(final TileCoordinates centerTile) {
        List<TileCoordinates> newTiles = preProccessMatrix(generateMatrixItems(centerTile, MATRIX_WIDTH, true));
        Log.e("matrix", "not loaded size: " + newTiles.size());
        for (TileCoordinates tile : newTiles) {
            renderTile(tile);
        }
    }

    public void setTrafficEnable(boolean isEnable) {
        for (GroundOverlayItem item : matrix.values()) {
            item.setTrafficEnable(isEnable);
        }
    }

    /**
     *
     * @param tileItems
     * @return not loaded tile
     */
    private List<TileCoordinates> preProccessMatrix(List<TileCoordinates> tileItems) {
        List<TileCoordinates> newTiles = new ArrayList<>();
        //HashMap<TileCoordinates, GroundOverlayMatrixItem> removeTiles = new HashMap<>();
        HashMap<TileCoordinates, GroundOverlayItem> newMatrix = new HashMap<>();
        for (TileCoordinates tile : tileItems) {
            if (matrix.containsKey(tile)) {
                newMatrix.put(tile, matrix.get(tile));
                matrix.remove(tile);
            } else {
                newTiles.add(tile);
            }
        }

        Iterator<GroundOverlayItem> removeItems = matrix.values().iterator();
        GroundOverlayItem matrixItem;
        for (TileCoordinates tile : newTiles) {
            if (removeItems.hasNext()) {
                matrixItem = removeItems.next();
                matrixItem.overlayInit();
                newMatrix.put(tile, matrixItem);
            } else {
                newMatrix.put(tile, new GroundOverlayItem());
            }
        }
        matrix = newMatrix;
        Log.e("matrix size", "" + matrix.size());
        return newTiles;
    }

    /**
     * TODO: Load tile to render
     * @param tile
     */
    private void renderTile(final TileCoordinates tile) {
        RetrofitClient.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (!render(tile)) {
                    tileRenderHandler.render(tile, trafficDataLoader.loadTrafficDataFromServer(tile));
                }
            }
        });
    }

    // load data from database
    // if have data => render to map => return true
    // else => return false
    private boolean render(TileCoordinates tile) {
        if (!trafficDataLoader.isDataNotLoaded(tile)) {
            List<StatusRenderDataEntity> dataList = trafficDataLoader.loadDataFromLocal(tile);
            tileRenderHandler.render(tile, dataList);
            return true;
        }
        return false;
    }

    public static List<TileCoordinates> generateMatrixItems (TileCoordinates centerTile, int matrixWidth, boolean includeCenter) {
        List<TileCoordinates> tileCoordinatesList = new ArrayList<>();
        List<TileCoordinates> rowItems = getRowItems(centerTile, matrixWidth);
        for (TileCoordinates item : rowItems) {
            tileCoordinatesList.addAll(getColumnItems(item, matrixWidth));
        }
        if (!includeCenter) {
            tileCoordinatesList.remove(centerTile);
        }
        return tileCoordinatesList;
    }

    /**
     * get row items (contain center item)
     * @param center
     * @return
     */
    private static List<TileCoordinates> getRowItems (TileCoordinates center, int matrixWidth) {
        List<TileCoordinates> tiles = new ArrayList<>();
        tiles.add(center);
        try {
            TileCoordinates leftTemp = center;
            TileCoordinates rightTemp = center;
            for (int i = 0; i < matrixWidth / 2; i++) {
                leftTemp = leftTemp.getTileLeft();
                rightTemp = rightTemp.getTileRight();
                tiles.add(leftTemp);
                tiles.add(rightTemp);
            }
        } catch (TileCoordinates.TileCoordinatesNotValid tileCoordinatesNotValid) {
            tileCoordinatesNotValid.printStackTrace();
        }
        return tiles;
    }

    /**
     * get column items (contain center item)
     * @param center
     * @return
     */
    private static List<TileCoordinates> getColumnItems (TileCoordinates center, int matrixWidth) {
        List<TileCoordinates> tiles = new ArrayList<>();
        tiles.add(center);
        try {
            TileCoordinates topTemp = center;
            TileCoordinates botTemp = center;
            for (int i = 0; i < matrixWidth / 2; i++) {
                topTemp = topTemp.getTileTop();
                botTemp = botTemp.getTileBot();
                tiles.add(topTemp);
                tiles.add(botTemp);
            }
        } catch (TileCoordinates.TileCoordinatesNotValid tileCoordinatesNotValid) {
            tileCoordinatesNotValid.printStackTrace();
        }
        return tiles;
    }

    // TODO:
    public void refresh(final TileCoordinates centerTile) {}
}
