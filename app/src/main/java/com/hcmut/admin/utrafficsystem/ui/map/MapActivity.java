package com.hcmut.admin.utrafficsystem.ui.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.hcmut.admin.utrafficsystem.MyApplication;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.business.GPSForegroundServiceHandler;
import com.hcmut.admin.utrafficsystem.business.UserLocation;
import com.hcmut.admin.utrafficsystem.business.VersionUpdater;
import com.hcmut.admin.utrafficsystem.business.trafficmodule.TrafficRenderModule;
import com.hcmut.admin.utrafficsystem.model.AndroidExt;
import com.hcmut.admin.utrafficsystem.model.MarkerListener;
import com.hcmut.admin.utrafficsystem.model.User;
import com.hcmut.admin.utrafficsystem.business.CallPhone;
import com.hcmut.admin.utrafficsystem.business.PhotoUploader;
import com.hcmut.admin.utrafficsystem.service.AppForegroundService;
import com.hcmut.admin.utrafficsystem.ui.viewReport.ViewReportFragment;
import com.hcmut.admin.utrafficsystem.ui.voucher.buypoint.BuyPointFragment;
import com.hcmut.admin.utrafficsystem.util.ClickDialogListener;
import com.hcmut.admin.utrafficsystem.util.GiftUtil;
import com.hcmut.admin.utrafficsystem.util.LocationCollectionManager;
import com.hcmut.admin.utrafficsystem.util.SharedPrefUtils;
import com.stepstone.apprating.listener.RatingDialogListener;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import vn.momo.momo_partner.AppMoMoLib;

/**
 * Created by User on 10/2/2017.
 */

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        RatingDialogListener {
    private static final int TURN_ON_GPS_REQUEST = 2000;

    private MyApplication myapp;

    public static User currentUser;
    public static GoogleMap mMap;
    public static AndroidExt androidExt;
    public static GiftUtil giftUtil;
    private Date pressTime;
    private TrafficRenderModule trafficRenderModule;
    private boolean isRenderStatus = true;

    // Listener
    private ViewReportFragment.OnReportMakerClick reportMakerClickListener;
    private MarkerListener markerListener;
    private RatingDialogListener ratingDialogListener;
    private List<OnMapReadyListener> onMapReadyListeners = new ArrayList<>();
    private PhotoUploader photoUploader;

    private SupportMapFragment mapFragment;
    private BuyPointFragment buyPointFragment;
    private BottomTab bottomTab;
    private BottomNavigation bottomNavigation;
    private FrameLayout flFragment;
    private androidx.fragment.app.Fragment homeFragment;
    private androidx.fragment.app.Fragment contributionFragment;
    private androidx.fragment.app.Fragment viewReportFragment;
    private androidx.fragment.app.Fragment accountReportFragment;
    MarkerOptions marker = new MarkerOptions().icon(null);

    public void setUserReportMarkerListener(ViewReportFragment.OnReportMakerClick listener) {
        reportMakerClickListener = listener;
    }

    public void setMarkerListener(MarkerListener markerListener) {
        this.markerListener = markerListener;
    }
    public void setBuyPointFragment(BuyPointFragment buyPointFramgent) {
        this.buyPointFragment = buyPointFramgent;
    }
    public void addMapReadyCallback(@NotNull OnMapReadyListener onMapReadyListener) {
        if (mMap != null) {
            onMapReadyListener.onMapReady(mMap);
        } else {
            onMapReadyListeners.add(onMapReadyListener);
        }
    }

    public boolean isRenderStatus() {
        return isRenderStatus;
    }

    public void setRatingDialogListener(RatingDialogListener ratingDialogListener) {
        this.ratingDialogListener = ratingDialogListener;
    }

    public void registerCameraPhotoHandler(PhotoUploader photoUploader) {
        this.photoUploader = photoUploader;
    }

    public void unRegisterCameraPhotoHandler() {
        this.photoUploader = null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        myapp = (MyApplication) this.getApplicationContext();
        currentUser = SharedPrefUtils.getUser(MapActivity.this);
        androidExt = new AndroidExt();
        giftUtil = new GiftUtil();

        VersionUpdater.checkNewVersion(this);
        if (GPSForegroundServiceHandler.requireLocationPermission(this)) {
            addCotrols();
            addEvents();
        }
    }

    private void addCotrols() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.gg_api_key));
        }

        homeFragment = getSupportFragmentManager().findFragmentById(R.id.homeFragmentTab);
        contributionFragment = getSupportFragmentManager().findFragmentById(R.id.contributionFragmentTab);
        viewReportFragment = getSupportFragmentManager().findFragmentById(R.id.viewReportFragmentTab);
        accountReportFragment = getSupportFragmentManager().findFragmentById(R.id.accountFragmentTab);

        View homeTabWrapper = findViewById(R.id.homeTabWrapper);
        View contributionTabWrapper = findViewById(R.id.contributionTabWrapper);
        View viewReportTabWrapper = findViewById(R.id.viewReportTabWrapper);
        View accountTabWrapper = findViewById(R.id.accountTabWrapper);
        bottomTab = new BottomTab.Builder(R.id.homeTabId)
                .addTab(R.id.contributeTabId, contributionTabWrapper)
                .addTab(R.id.homeTabId, homeTabWrapper)
                .addTab(R.id.viewReportTabId, viewReportTabWrapper)
                .addTab(R.id.accountTabId, accountTabWrapper)
                .build();
    }

    /**
     *
     * Add Events
     */
    private void addEvents() {
        boolean gpsCollectionSwithValue = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(getResources().getString(R.string.swGpsCollectionRef), true);
        if (gpsCollectionSwithValue) {
            EnableGPSAutoMatically();
        }

        AppForegroundService.toggleReportNoti(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(getResources().getString(R.string.swNotifyAroundRef), true), getApplicationContext());

        AppForegroundService.toggleDirectionNoti(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(getResources().getString(R.string.swNotifyMoveRef), true), getApplicationContext());

        AppForegroundService.toggleSleepWakeup(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(getResources().getString(R.string.swAutoSleepWakeupRef), true), getApplicationContext());

        flFragment = findViewById(R.id.flFragment);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        flFragment.setPadding(0, 0, 0, bottomNavigation.getNavigationHeight());
        bottomNavigation.setMenuItemSelectionListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(int menuItemId, int i1, boolean b) {
                bottomTab.showTab(menuItemId);
            }

            @Override
            public void onMenuItemReselect(int i, int i1, boolean b) {

            }
        });
    }

    public void setTrafficEnable(boolean isEnable) {
        if (trafficRenderModule != null) {
            isRenderStatus = isEnable;
            trafficRenderModule.setTrafficEnable(isEnable);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(TrafficRenderModule.MAX_ZOOM_LEVEL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        for (OnMapReadyListener listener : onMapReadyListeners) {
            listener.onMapReady(googleMap);
        }
        onMapReadyListeners.clear();
        trafficRenderModule = new TrafficRenderModule(getApplicationContext(), mMap, mapFragment);
        trafficRenderModule.startStatusRenderTimer();
        UserLocation latestLocation = SharedPrefUtils.getLatestLocation(getApplicationContext());
        if (latestLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(latestLocation.getLatitude(), latestLocation.getLongitude()), 13));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(10.790643, 106.652569), 13));
        }
        giftUtil.addGift(MapActivity.this,getApplicationContext(),mMap);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                try {
                    String[] strTag = marker.getTag().toString().split("-");
                    

                    switch (strTag[0]) {
                        case ViewReportFragment.REPORT_RATING: {
                            if (reportMakerClickListener != null) {
                                reportMakerClickListener.onClick(marker);
                            }
                            break;
                        }
                        case MarkerListener.REPORT_ARROW:
                            if (markerListener != null) {
                                markerListener.onClick(marker);
                            }
                        case "GIFT":
                            giftUtil.checkGift(MapActivity.this,getApplicationContext(),marker,strTag[1]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    private void EnableGPSAutoMatically() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // **************************
        builder.setAlwaysShow(true); // this is the key ingredient
        // **************************

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result
                        .getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // GPS was turned on
                        GPSForegroundServiceHandler.initLocationService(MapActivity.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MapActivity.this, TURN_ON_GPS_REQUEST);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        toast("Setting change not allowed");
                        break;
                }
            }
        });
    }
    private void toast(String message) {
        try {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (trafficRenderModule != null) {
            trafficRenderModule.startStatusRenderTimer();
        }
    }

    @Override
    protected void onResume() {
        myapp.setCurrentActivity(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        clearActivity();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (trafficRenderModule != null) {
            trafficRenderModule.stopStatusRenderTimer();
        }
    }

    @Override
    protected void onDestroy() {
        clearActivity();
        mMap = null;
        androidExt = null;
        AppForegroundService.path_id = null;
        SharedPrefUtils.saveLatestLocation(
                getApplicationContext(),
                LocationCollectionManager.getInstance(getApplicationContext()).getLastUserLocation());
        super.onDestroy();
    }

    private void clearActivity() {
        Activity currActivity = myapp.getCurrentActivity();
        if (this.equals(currActivity))
            myapp.setCurrentActivity(null);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity == null) return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void showBottomNav() {
        if (bottomNavigation.getVisibility() != View.VISIBLE) {
            bottomNavigation.setVisibility(View.VISIBLE);
            flFragment.setPadding(0, 0, 0, bottomNavigation.getNavigationHeight());
        }
    }

    public void hideBottomNav() {
        if (bottomNavigation.getVisibility() == View.VISIBLE) {
            bottomNavigation.setVisibility(View.GONE);
            flFragment.setPadding(0, 0, 0, 0);
        }
    }

    public int getBottomNavHeight() {
        return bottomNavigation.getNavigationHeight();
    }

    public GoogleMap getGoogleMap() {
        return mMap;
    }

    public void setGoogleMap(GoogleMap map) {
        this.mMap = map;
    }

    @Override
    public void onBackPressed() {
        if (onNavigationBackPress(homeFragment) ||
                onNavigationBackPress(contributionFragment) ||
                onNavigationBackPress(viewReportFragment) ||
                onNavigationBackPress(accountReportFragment)) {
            return;
        }

        Date currentTime = new Date();
        if (pressTime == null) {
            pressTime = currentTime;
            Toast.makeText(this, "Nhấn lần nữa để thoát!", Toast.LENGTH_SHORT).show();
            return;
        }
        long alpha = TimeUnit.MILLISECONDS.toMillis(currentTime.getTime() - pressTime.getTime());
        if (alpha < 8000) {
            super.onBackPressed();
        } else {
            pressTime = currentTime;
            Toast.makeText(this, "Nhấn lần nữa để thoát!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean onNavigationBackPress(androidx.fragment.app.Fragment fragment) {
        try {
            OnBackPressCallback callback = (OnBackPressCallback) fragment
                    .getChildFragmentManager()
                    .getFragments().get(0);
            callback.onBackPress();
            return true;
        } catch (Exception e) {}
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case PhotoUploader.IMAGE_REQUEST:
                Objects.requireNonNull(photoUploader).onActivityResult(getApplicationContext(), resultCode, data);
                return;
            case TURN_ON_GPS_REQUEST:
                if (resultCode == RESULT_OK) {
                    // GPS was turned on
                    GPSForegroundServiceHandler.initLocationService(MapActivity.this);
                } else {
                    // GPS is not turn on
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(MapActivity.this)
                            .edit();
                    editor.putBoolean(getResources().getString(R.string.swGpsCollectionRef), false);
                    editor.apply();
                }
                return;
        }
        if(requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO) {
            buyPointFragment.onActivityResult(requestCode,resultCode,data);
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GPSForegroundServiceHandler.MUTILE_PERMISSION_REQUEST:
                GPSForegroundServiceHandler.handleAppForgroundPermission(MapActivity.this, requestCode, permissions, grantResults);
                break;
            case CallPhone.CALL_PHONE_CODE:
                boolean isHavePermission = CallPhone.handleCallPhonePermission(grantResults);
                if (isHavePermission) {
                    CallPhone.makeAPhoneCall(MapActivity.this);
                }
                break;
            case PhotoUploader.IMAGE_PERMISSION_CODE:
                Objects.requireNonNull(photoUploader).onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults,
                        MapActivity.this);
                break;
            case GPSForegroundServiceHandler.LOCATION_PERMISSION_REQUEST:
                if (!GPSForegroundServiceHandler.handleLocationPermissionResult(requestCode, permissions, grantResults)) {
                    androidExt.showNotifyDialog(
                            MapActivity.this,
                            "Ứng dụng sẽ tắt do không được cấp quyền truy cập vị trí, vui lòng thử lại!",
                            new ClickDialogListener.OK() {
                                @Override
                                public void onCLickOK() {
                                    MapActivity.this.finishAndRemoveTask();
                                }
                            });
                } else {
                    addCotrols();
                    addEvents();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onNegativeButtonClicked() {
        if (ratingDialogListener != null) {
            ratingDialogListener.onNegativeButtonClicked();
        }
    }

    @Override
    public void onNeutralButtonClicked() {
        if (ratingDialogListener != null) {
            ratingDialogListener.onNeutralButtonClicked();
        }
    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {
        if (ratingDialogListener != null) {
            ratingDialogListener.onPositiveButtonClicked(i, s);
        }
    }

    public interface OnMapReadyListener {
        void onMapReady(GoogleMap googleMap);
    }

    public interface OnBackPressCallback {
        void onBackPress();
    }


}