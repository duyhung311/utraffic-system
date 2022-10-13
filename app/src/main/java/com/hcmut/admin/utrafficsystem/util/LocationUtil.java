package com.hcmut.admin.utrafficsystem.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationUtil {

    public static Integer[] setLastVelocity(int velocity, Integer[] velocities) {
        int notNull = 0;
        for (Integer integer : velocities) {
            if (integer == null) {
                break;
            }
            notNull++;
        }
        if (notNull < velocities.length) {
            velocities[notNull] = velocity;
        } else {
            for (int i = 0; i < velocities.length; i++) {
                if (i + 1 == velocities.length) {
                    velocities[i] = velocity;
                    break;
                }
                velocities[i] = velocities[i + 1];
            }
        }
        return velocities;
    }

    public static int getAvgVelocity(Integer[] velocities) {
        int avgVelocity = 0;
        for (int i = 0; i < velocities.length; i++) {
            if (velocities[i] == null) return avgVelocity / (i + 1);
            if (i + 1 == velocities.length) {
                avgVelocity = avgVelocity + velocities[i];
                return avgVelocity / velocities.length;
            }
            avgVelocity = avgVelocity + velocities[i];
        }
        return avgVelocity;
    }

    /*
  ----------------------------GET ADDRESS BY ID---------------------------
*/
    public static String getAddressByLatLng(Context context, LatLng latLng) {
        Locale locale = new Locale.Builder().setLanguage("vi").build();
        Geocoder gcd = new Geocoder(context, locale);
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            return addresses.get(0).getAddressLine(0);
        } else {
            return "";
        }
    }

    public static LatLng getDestinationLocation(LatLng currentLatLng, float bearing) {
        double R = 6378.1;
        double brng = Math.toRadians(bearing);
        double d = 0.02; //km

        double currentLat = Math.toRadians(currentLatLng.latitude);
        double currentLng = Math.toRadians(currentLatLng.longitude);

        double lat = Math.asin( Math.sin(currentLat)*Math.cos(d/R) +
             Math.cos(currentLat)*Math.sin(d/R)*Math.cos(brng));

        double lng = currentLng + Math.atan2(Math.sin(brng)*Math.sin(d/R)*Math.cos(currentLat),
                     Math.cos(d/R)-Math.sin(currentLat)*Math.sin(lat));

        lat = Math.toDegrees(lat);
        lng = Math.toDegrees(lng);
        return new LatLng(lat, lng);
    }
}
