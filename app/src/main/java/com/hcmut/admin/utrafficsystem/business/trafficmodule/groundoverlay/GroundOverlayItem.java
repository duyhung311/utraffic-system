package com.hcmut.admin.utrafficsystem.business.trafficmodule.groundoverlay;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.hcmut.admin.utrafficsystem.business.TileCoordinates;
import com.hcmut.admin.utrafficsystem.util.MyLatLngBoundsUtil;

import org.jetbrains.annotations.NotNull;

public class GroundOverlayItem {
    public static final String LOADED_OVERLAY = "LOADED_OVERLAY";
    public static final String LOADING_OVERLAY = "LOADING_OVERLAY";
    public static final String INIT_OVERLAY = "INIT_OVERLAY";
    public static final String LOAD_FAIL_OVERLAY = "LOAD_FAIL_OVERLAY";

    private GroundOverlay groundOverlay;
    private String state = INIT_OVERLAY;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public GroundOverlayItem() {}

    public void invalidate (@NotNull Bitmap bitmap, @NotNull TileCoordinates target, @NotNull final GoogleMap googleMap) {
        if (!state.equals(LOADED_OVERLAY)) {
            final GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();
            groundOverlayOptions.image(BitmapDescriptorFactory.fromBitmap(bitmap));
            groundOverlayOptions.positionFromBounds(MyLatLngBoundsUtil.tileToLatLngBound(target));
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    state = LOADED_OVERLAY;
                    groundOverlay = googleMap.addGroundOverlay(groundOverlayOptions);
                }
            });
        }
    }

    public void invalidate (@NotNull final Bitmap bitmap, @NotNull final TileCoordinates tileCoordinates, @NotNull final GroundOverlayItem idleMatrixItem) {
        if (!state.equals(LOADED_OVERLAY)) {
            final GroundOverlay idleOverlay = idleMatrixItem.getGroundOverlay();
            final BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    idleOverlay.setImage(bitmapDescriptor);
                    idleOverlay.setPositionFromBounds(MyLatLngBoundsUtil.tileToLatLngBound(tileCoordinates));
                    groundOverlay = idleOverlay;
                    state = LOADED_OVERLAY;
                }
            });
        }
    }

    public void invalidateItself(@NotNull final Bitmap bitmap, @NotNull final TileCoordinates tile, @NotNull final GoogleMap googleMap) {
        if (groundOverlay != null) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    groundOverlay.setImage(BitmapDescriptorFactory.fromBitmap(bitmap));
                    groundOverlay.setPositionFromBounds(MyLatLngBoundsUtil.tileToLatLngBound(tile));
                    state = LOADED_OVERLAY;
                    try {
                        bitmap.recycle();
                    } catch (Exception e) {}
                }
            });
        } else {
            final GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();
            groundOverlayOptions.image(BitmapDescriptorFactory.fromBitmap(bitmap));
            groundOverlayOptions.positionFromBounds(MyLatLngBoundsUtil.tileToLatLngBound(tile));
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    groundOverlay = googleMap.addGroundOverlay(groundOverlayOptions);
                    state = LOADED_OVERLAY;
                    try {
                        bitmap.recycle();
                    } catch (Exception e) {}
                }
            });
        }
    }

    public GroundOverlay getGroundOverlay() {
        return groundOverlay;
    }

    public void setGroundOverlay(GroundOverlay groundOverlay) {
        this.groundOverlay = groundOverlay;
    }

    public boolean isOvelayLoaded() {
        return groundOverlay != null;
    }

    public boolean isOverlayInit() {
        return state.equals(INIT_OVERLAY);
    }

    public boolean isNotLoaded() {
        return state.equals(INIT_OVERLAY) || state.equals(LOAD_FAIL_OVERLAY);
    }

    public String getState() {
        return state;
    }

    private void setState(String state) {
        this.state = state;
    }

    public void overlayInit() {
        setState(INIT_OVERLAY);
    }

    public void ovelayLoaded() {
        setState(LOADED_OVERLAY);
    }

    public void ovelayLoading() {
        setState(LOADING_OVERLAY);
    }

    public void overlayLoadFail() {
        setState(LOAD_FAIL_OVERLAY);
    }

    public void setTrafficEnable(boolean isEnable) {
        if (groundOverlay != null) {
            groundOverlay.setVisible(isEnable);
        }
    }
}
