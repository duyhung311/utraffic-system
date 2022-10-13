package com.hcmut.admin.utrafficsystem.business;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import com.hcmut.admin.utrafficsystem.util.LocationServiceAlarmUtil;
import com.hcmut.admin.utrafficsystem.util.MovingDetection;
import com.hcmut.admin.utrafficsystem.util.TrafficNotificationFactory;

import static com.hcmut.admin.utrafficsystem.util.TrafficNotificationFactory.STOPPED_NOTIFICATION_ID;

public class SleepWakeupLocationService {
    private static final int STOP_MAX_TIMES = 15;

    private int stopCount = 0;
    private MovingDetection movingDetection = new MovingDetection();
    private StopServiceEvent stopServiceEvent;
    private Context context;

    public SleepWakeupLocationService(Context context) {
        this.context = context;
    }

    public void setStopServiceEvent(StopServiceEvent stopServiceEvent) {
        this.stopServiceEvent = stopServiceEvent;
    }

    public void repare() {
        stopCount = 0;
        movingDetection = new MovingDetection();
    }

    public void handleSleepOrWakeupService(UserLocation currUserLocation) {
        movingDetection.setCurrLocation(currUserLocation);
        if (!movingDetection.isMoving()) {
            stopCount++;
        } else {
            stopCount = 0;
        }
        Log.e("stoping", "stop count: " + stopCount);

        // nếu người dùng không di chuyển ra khỏi vị trí trong
        // STOP_MAX_TIMES lần lấy tọa độ, thì xác định người dùng không di chuyển
        // => dừng LocationService
        if (stopCount > STOP_MAX_TIMES) {
            // user is not move
            // stop service
            if (stopServiceEvent != null) {
                stopServiceEvent.onStop();
            }
            TrafficNotificationFactory trafficNotificationFactory = TrafficNotificationFactory
                    .getInstance(context);
            Notification notification = trafficNotificationFactory
                    .getStoppedServiceNotification(context);

            // send notification service have stopped
            trafficNotificationFactory.sendNotification(notification, STOPPED_NOTIFICATION_ID);
            LocationServiceAlarmUtil.setLocationAlarm(context);     // set up alarm to start service again
            stopCount = 0;
        }
    }

    public interface StopServiceEvent {
        void onStop();
    }
}
