package com.hcmut.admin.utrafficsystem.tbt.data;

import android.location.Location;

public class LocationWithDisTime {
    public final Location location;
    public final float distance;
    public final float time;

    public LocationWithDisTime(Location location, float distance, float time) {
        this.location = location;
        this.distance = distance;
        this.time = time;
    }
}
