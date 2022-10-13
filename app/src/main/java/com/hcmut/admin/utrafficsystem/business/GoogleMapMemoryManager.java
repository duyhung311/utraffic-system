package com.hcmut.admin.utrafficsystem.business;

import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;

import java.lang.ref.WeakReference;

public class GoogleMapMemoryManager {
    private WeakReference<SupportMapFragment> mapFragmentWeakReference;

    private int moveCount = 0;
    private int lastZoom = 5;

    public GoogleMapMemoryManager(SupportMapFragment mapFragment) {
        this.mapFragmentWeakReference = new WeakReference<>(mapFragment);
    }

    public void onMapMove (float zoom) {
        int currentZoom = (int) zoom;
        Log.e("test", "zoom: " + currentZoom);
        if (currentZoom != lastZoom) {
            lastZoom = currentZoom;
            onLowMemory();
            Log.e("test", "lowMemory");
        } else {
            if (moveCount > getLimitMoveCount(currentZoom)) {
                onLowMemory();
                Log.e("test", "lowMemory");
            } else {
                moveCount++;
            }
        }
        Log.e("test", "count: " + moveCount);
    }

    public void onLowMemory () {
        SupportMapFragment mapFragment = mapFragmentWeakReference.get();
        if (mapFragment != null) {
            mapFragment.onLowMemory();
            moveCount = 0;
        }
    }

    private int getLimitMoveCount (int zoom) {
        switch (zoom) {
            case 15:
                return 80;
            case 16:
                return 50;
            case 17:
                return 15;
            case 18:
                return 20;
            case 19:
                return 18;
            case 20:
                return 14;
            case 21:
                return 10;
        }
        return 200;
    }
}
