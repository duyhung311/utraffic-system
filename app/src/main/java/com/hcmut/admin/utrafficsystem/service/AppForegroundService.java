package com.hcmut.admin.utrafficsystem.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hcmut.admin.utrafficsystem.business.SleepWakeupLocationService;
import com.hcmut.admin.utrafficsystem.util.LocationCollectionManager;
import com.hcmut.admin.utrafficsystem.util.TrafficNotificationFactory;

public class AppForegroundService extends Service {

    private static final int SERVICE_ID = 110;

    public static final int SERVICE_STOP_REQUEST_CODE = 111;
    public static final String STOP_FOREGROUND_ACTION = "com.example.traffic.service.LocationService.STOP_FOREGROUND_ACTION";

    private static LocationCollectionManager locationCollectionManager;
    private HandlerThread locationHandlerThread;
    private static boolean isDireactionNotiOn = false;
    private static boolean isReportNotiOn = false;
    public static String path_id = null;
    private static MutableLiveData<Boolean> movingStateLiveData = new MutableLiveData<>();

    public static LiveData<Boolean> getMovingStateLiveData () {
        return movingStateLiveData;
    }

    public static boolean isUpdateCurrentLocation() {
        Log.e("path_id", "" + path_id);
        return ((path_id != null) && isDireactionNotiOn) || isReportNotiOn;
    }

    public static void toggleDirectionNoti(boolean value, Context context) {
        isDireactionNotiOn = value;
        locationCollectionManager = LocationCollectionManager.getInstance(context);
        if (!value) {
            locationCollectionManager.stopNotification();
        }
    }

    public static void toggleReportNoti(boolean value, Context context) {
        isReportNotiOn = value;
        locationCollectionManager = LocationCollectionManager.getInstance(context);
        if (!value) {
            locationCollectionManager.stopNotification();
        }
    }

    public static void toggleSleepWakeup(boolean value, Context context) {
        locationCollectionManager = LocationCollectionManager.getInstance(context);
        locationCollectionManager.toggleSleepWakeup(value);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            startForeground(SERVICE_ID, TrafficNotificationFactory
                    .getInstance(getApplicationContext())
                    .getForegroundServiceNotification(getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // tracking user location
        locationHandlerThread = new HandlerThread("Location Listener thread");
        locationHandlerThread.start();
        locationCollectionManager = LocationCollectionManager.getInstance(getApplicationContext());
        locationCollectionManager.beginTraceLocation(locationHandlerThread.getLooper());
        locationCollectionManager.setStopServiceEvent(new SleepWakeupLocationService.StopServiceEvent() {
            @Override
            public void onStop() {
                stopForeground(true);
                stopSelf();
            }
        });
        movingStateLiveData.postValue(true);
        Log.e("app service", "created");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (STOP_FOREGROUND_ACTION.equals(intent.getAction())) {
            stopForeground(true);
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            locationCollectionManager.endTraceLocation();
            locationHandlerThread.quitSafely();
            locationCollectionManager = null;
            locationHandlerThread = null;
        } catch (Exception e) {}
        movingStateLiveData.postValue(false);
        Log.e("Service", "stop");
    }
}
