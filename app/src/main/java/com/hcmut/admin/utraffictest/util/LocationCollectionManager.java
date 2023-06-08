package com.hcmut.admin.utraffictest.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hcmut.admin.utraffictest.repository.remote.model.BaseResponse;
import com.hcmut.admin.utraffictest.repository.remote.model.request.ReportRequest;
import com.hcmut.admin.utraffictest.business.SleepWakeupLocationService;
import com.hcmut.admin.utraffictest.business.UserLocation;
import com.hcmut.admin.utraffictest.repository.remote.API.APIService;
import com.hcmut.admin.utraffictest.repository.remote.RetrofitClient;
import com.hcmut.admin.utraffictest.repository.remote.model.response.ReportResponse;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationCollectionManager {

    private static final int INTERVAL = 12000;
    private static final int FASTEST_INTERVAL = 8000;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback callback;
    private static LocationCollectionManager locationCollectionManager;

    private Context context;
    private APIService apiService;
    private UserLocation lastUserLocation;
    private boolean isSleepWakeupOn = true;

    private SleepWakeupLocationService sleepWakeupLocationService;

    private LocationCollectionManager(Context context) {
        this.context = context.getApplicationContext();
        fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(context.getApplicationContext());
        apiService = RetrofitClient.getApiService();
        sleepWakeupLocationService = new SleepWakeupLocationService(context);
    }

    public void toggleSleepWakeup(boolean isTurnOn) {
        if (isTurnOn) {
            sleepWakeupLocationService = new SleepWakeupLocationService(context);
            sleepWakeupLocationService.repare();
        }
        isSleepWakeupOn = isTurnOn;
        Log.e("sleep", "" + isSleepWakeupOn);
    }

    public void setStopServiceEvent(SleepWakeupLocationService.StopServiceEvent stopServiceEvent) {
        try {
            sleepWakeupLocationService.setStopServiceEvent(stopServiceEvent);
        } catch (Exception e) {}
    }

    public static LocationCollectionManager getInstance(Context context) {
        if (locationCollectionManager == null) {
            locationCollectionManager = new LocationCollectionManager(context);
        }
        return locationCollectionManager;
    }

    public UserLocation getLastUserLocation() {
        return lastUserLocation;
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(OnSuccessListener<Location> onSuccessListener) {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(onSuccessListener)
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lastUserLocation = new UserLocation(location.getLatitude(), location.getLongitude());
                        }
                    }
                });
    }

    @SuppressLint("MissingPermission")
    public void beginTraceLocation(Looper looper) {
        LocationRequest request = LocationRequest.create();
        request.setInterval(INTERVAL);
        request.setFastestInterval(FASTEST_INTERVAL);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        sleepWakeupLocationService = new SleepWakeupLocationService(context);
        sleepWakeupLocationService.repare();
        this.callback = new LocationReceiverCallback();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.requestLocationUpdates(request, this.callback, looper);
        }
    }

    public void endTraceLocation() {
        if (fusedLocationProviderClient != null && callback != null) {
            fusedLocationProviderClient.removeLocationUpdates(callback);
            callback = null;
        }
    }

    public class LocationReceiverCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                UserLocation currUserLocation = new UserLocation(location);
                postReport(currUserLocation);
                lastUserLocation = currUserLocation;
                if (isSleepWakeupOn && sleepWakeupLocationService != null) {
                    sleepWakeupLocationService.handleSleepOrWakeupService(currUserLocation);
                }
            }
        }
    }

    private void postReport(UserLocation currUserLocation) {
        if (lastUserLocation == null) return;
        String accessAuth = MapActivity.currentUser.getAccessToken();
        ReportRequest reportRequest = new ReportRequest(lastUserLocation, currUserLocation);
        reportRequest.checkUpdateCurrentLocation(context);
        apiService.postGPSTrafficReport(accessAuth, reportRequest).enqueue(new Callback<BaseResponse<ReportResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<ReportResponse>> call, Response<BaseResponse<ReportResponse>> response) {
                Log.e("post GPS report", response.toString());
            }

            @Override
            public void onFailure(Call<BaseResponse<ReportResponse>> call, Throwable t) {
                Log.e("post GPS report", "fail");
            }
        });
    }

    public boolean isGPSEnabled() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * query to stop notification to server
     */
    public void stopNotification() {
        getCurrentLocation(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null && lastUserLocation == null) {
                    return;
                }
                UserLocation userLocation;
                if (location != null) {
                    userLocation = new UserLocation(location);
                } else {
                    userLocation = lastUserLocation;
                }
                String accessAuth = MapActivity.currentUser.getAccessToken();
                ReportRequest reportRequest = ReportRequest.getStopNotificationModel(userLocation, context);
                apiService.postGPSTrafficReport(accessAuth, reportRequest).enqueue(new Callback<BaseResponse<ReportResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<ReportResponse>> call, Response<BaseResponse<ReportResponse>> response) {
                        Log.e("post GPS report", response.toString());
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<ReportResponse>> call, Throwable t) {
                        Log.e("post GPS report", "fail");
                    }
                });
            }
        });
    }
}
