package com.hcmut.admin.utraffictest.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hcmut.admin.utraffictest.model.Atm;
import com.hcmut.admin.utraffictest.model.HealthFacility;

public class MarkerCreating {
    private LatLng latLng;
    private Marker marker;

    public MarkerCreating (LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLocation() {
        return latLng;
    }

    public void createMarker(Context context, GoogleMap googleMap, Integer iconSrc, boolean isMoveToCurrentLocation, boolean animated) {
        if (googleMap == null) return;
        if (marker != null) {
            marker.remove();
        }
        BitmapDescriptor bitmapDescriptor = (iconSrc != null) ? bitmapDescriptorFromVector(context, iconSrc) : BitmapDescriptorFactory.defaultMarker();
        marker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(bitmapDescriptor));

        if (isMoveToCurrentLocation) {
            // move camera to current location
            if (animated) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            }
        }
    }

    public void createMarker(Context context, GoogleMap googleMap, Integer iconSrc, boolean isMoveToCurrentLocation, boolean animated, String title) {
        if (googleMap == null) return;
        if (marker != null) {
            marker.remove();
        }
        BitmapDescriptor bitmapDescriptor = (iconSrc != null) ? bitmapDescriptorFromVector(context, iconSrc) : BitmapDescriptorFactory.defaultMarker();
        marker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(bitmapDescriptor));

        marker.showInfoWindow();

        if (isMoveToCurrentLocation) {
            // move camera to current location
            if (animated) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            }
        }
    }

    public void createHealthFacilityMarker(Context context,final GoogleMap googleMap, Integer iconSrc, boolean isMoveToCurrentLocation, boolean animated,
                                           HealthFacility healthFacility, float zIndex ) {
        if (googleMap == null) return;
        if (marker != null) {
            marker.remove();
        }
        BitmapDescriptor bitmapDescriptor = (iconSrc != null) ? bitmapDescriptorFromVector(context, iconSrc) : BitmapDescriptorFactory.defaultMarker();
        marker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(healthFacility.getName())
                .icon(bitmapDescriptor)
                .zIndex(zIndex));

        marker.setTag(healthFacility);

        if (isMoveToCurrentLocation) {
            // move camera to current location
            if (animated) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            }
        }
    }

    public void createAtmMarker(Context context,final GoogleMap googleMap, Integer iconSrc, boolean isMoveToCurrentLocation, boolean animated, Atm atm, float zIndex ) {
        if (googleMap == null) return;
        if (marker != null) {
            marker.remove();
        }
        BitmapDescriptor bitmapDescriptor = (iconSrc != null) ? bitmapDescriptorFromVector(context, iconSrc) : BitmapDescriptorFactory.defaultMarker();
        marker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(atm.getName())
                .icon(bitmapDescriptor)
                .zIndex(zIndex));

        marker.setTag(atm);
        if (isMoveToCurrentLocation) {
            // move camera to current location
            if (animated) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            }
        }
    }

    public void removeMarker() {
        if (marker != null) {
            marker.remove();
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int bgDrawableId) {
        try {
            Drawable background = ContextCompat.getDrawable(context, bgDrawableId);
            background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            background.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BitmapDescriptorFactory.defaultMarker();
    }
}
