package com.hcmut.admin.utrafficsystem.business;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class UserLocation {

    private static final float DEFAULT_ACCURACY = 3.0f;

    private int id;
    private double longitude;
    private double latitude;
    private Date timestamp;
    private Location location;

    public UserLocation(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = Calendar.getInstance().getTime();
        id = -1;
    }

    public UserLocation(Location location) {
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
        this.timestamp = Calendar.getInstance().getTime();
        id = -1;
        this.location = location;
    }

    public UserLocation(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        this.timestamp = Calendar.getInstance().getTime();
    }

    public UserLocation(int id, double longitude, double latitude) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = Calendar.getInstance().getTime();
    }

    public UserLocation() {
        id = -1;
        longitude = 0.0;
        latitude = 0.0;
        this.timestamp = Calendar.getInstance().getTime();
    }

    public static LatLng parseLatLng(UserLocation userLocation) {
        if (userLocation == null) {
            return null;
        }
        return new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
    }

    /**
     *
     * @param destLocation
     * @return: distance in meter
     */
    public float distanceTo (UserLocation destLocation) {
        float [] realDistance = new float[3];
        Location.distanceBetween(
                getLatitude(),
                getLongitude(),
                destLocation.getLatitude(),
                destLocation.getLongitude(),
                realDistance);
        Log.e("Distance", "" + realDistance[0]);
        return realDistance[0];
    }

    public static float distance(LatLng latLng1, LatLng latLng2) {
        float [] realDistance = new float[3];
        Location.distanceBetween(
                latLng1.latitude,
                latLng1.longitude,
                latLng2.latitude,
                latLng2.longitude,
                realDistance);
        return realDistance[0];
    }

    public float distanceToWithAccuracy (UserLocation destLocation) {
        return distanceTo(destLocation) - getAccuracy() - destLocation.getAccuracy();
    }

    public float getAccuracy() {
        if (location != null) {
            return location.getAccuracy();
        } else {
            return DEFAULT_ACCURACY;
        }
    }

    /**
     * return speed in km/h
     * @param prevLocation
     * @param currLocation
     * @return
     */
    public static float calculateSpeed(UserLocation prevLocation, UserLocation currLocation) {
        try {
            float distance = prevLocation.distanceTo(currLocation);
            long time = currLocation.getTimestamp().getTime() - prevLocation.getTimestamp().getTime();
            float speed = 3.6f * (distance / (time / 1000f));
            return Math.max(speed, 1.0f);
        } catch (Exception e) {
            return 1.0f;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getTimestampString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        return formatter.format(timestamp);
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "No" + id + ": Long: " + longitude + " ; Lati: " + latitude + " - Speed = " + timestamp;
    }
}
