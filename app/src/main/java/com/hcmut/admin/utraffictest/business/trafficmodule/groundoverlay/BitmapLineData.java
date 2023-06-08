package com.hcmut.admin.utraffictest.business.trafficmodule.groundoverlay;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

public class BitmapLineData {
    public final LatLng startLatLng;
    public final LatLng endLatLng;
    public final String color;

    public BitmapLineData(@NotNull LatLng startLatLng, @NotNull LatLng endLatLng, @NotNull String color) {
        this.startLatLng = startLatLng;
        this.endLatLng = endLatLng;
        this.color = color;
    }
}
