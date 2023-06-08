package com.hcmut.admin.utraffictest.business.trafficmodule.tileoverlay;

import com.hcmut.admin.utraffictest.business.TileCoordinates;

import java.util.HashMap;

public class LoadedTileManager {
    public static final String LOADED_STATE = "LOADED_STATE";
    public static final String LOADING_STATE = "LOADING_STATE";
    public static final String LOAD_FAIL_STATE = "LOAD_FAIL_STATE";

    private HashMap<TileCoordinates, String> loadedTiles = new HashMap<>();
    private static LoadedTileManager loadedTileManager;

    private LoadedTileManager() {}

    public static LoadedTileManager getInstance() {
        if (loadedTileManager == null) {
            loadedTileManager = new LoadedTileManager();
        }
        return loadedTileManager;
    }

    public synchronized void clear () {
        loadedTiles.clear();
    }

    public boolean isNotLoaded (TileCoordinates tile) {
        if (tile != null) {
            String tileState = loadedTiles.get(tile);
            if (tileState != null) {
                // TODO: schedule to reload 'fail tile'
                //return tileState.equals(LOAD_FAIL_STATE);
                return false;
            }
        }
        return true;
    }

    public synchronized boolean isLoaded (TileCoordinates tile) {
        if (tile != null) {
            String tileState = loadedTiles.get(tile);
            if (tileState != null) {
                // TODO: schedule to reload 'fail tile'
                return tileState.equals(LOADED_STATE);
            }
        }
        return false;
    }

    public synchronized boolean isLoading (TileCoordinates tile) {
        if (tile != null) {
            String tileState = loadedTiles.get(tile);
            if (tileState != null) {
                // TODO: schedule to reload 'fail tile'
                return tileState.equals(LOADING_STATE);
            }
        }
        return true;
    }

    public synchronized boolean isLoadingOrNotLoad(TileCoordinates tile) {
        if (tile != null) {
            String tileState = loadedTiles.get(tile);
            if (tileState != null) {
                // TODO: schedule to reload 'fail tile'
                return tileState.equals(LOADING_STATE);
            }
        }
        return true;
    }

    public synchronized void setLoadedTile (TileCoordinates tile) {
        loadedTiles.put(tile, LOADED_STATE);
    }

    public synchronized void setLoadFailTile(TileCoordinates tile) {
        loadedTiles.put(tile, LOAD_FAIL_STATE);
    }

    public synchronized void setLoadingTile (TileCoordinates tile) {
        loadedTiles.put(tile, LOADING_STATE);
    }
}
