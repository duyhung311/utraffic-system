package com.hcmut.admin.utrafficsystem.business.trafficmodule;

import android.util.Log;

import com.hcmut.admin.utrafficsystem.business.TileCoordinates;

import java.util.HashMap;
import java.util.Map;

public class DataLoadingState {
    private static final String LOADING_STATE = "loading";
    private Map<TileCoordinates, String> loadingTiles = new HashMap<>();
    private TileOverlayClearCallback clearCallback;

    public DataLoadingState(TileOverlayClearCallback clearCallback) {
        this.clearCallback = clearCallback;
    }

    public boolean isLoadingTileEmpty() {
        return loadingTiles.size() < 1;
    }

    public synchronized void putLoadingTile(TileCoordinates tile) {
        if (tile != null && !loadingTiles.containsKey(tile)) {
            loadingTiles.put(tile, LOADING_STATE);
        }
        Log.e("xxxxx", "put tile " + tile.toString());
    }

    public synchronized void removeLoadingTile(TileCoordinates tile) {
        if (loadingTiles.containsKey(tile)) {
            Log.e("xxxxx", "remove tile " + tile.toString());
            loadingTiles.remove(tile);
            if (isLoadingTileEmpty()) {
                Log.e("xxxxx", "reset tile overlay");
                if (clearCallback!= null) {
                    clearCallback.onClearTileCache();
                }
            }
        }
    }

    public void clear() {
        loadingTiles.clear();
    }

    public interface TileOverlayClearCallback {
        void onClearTileCache();
    }
}
