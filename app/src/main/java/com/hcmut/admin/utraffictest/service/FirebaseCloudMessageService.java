package com.hcmut.admin.utraffictest.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.model.AndroidExt;
import com.hcmut.admin.utraffictest.repository.remote.model.response.SegmentNotificationDataMessage;
import com.hcmut.admin.utraffictest.util.TrafficNotificationFactory;
import com.hcmut.admin.utraffictest.ui.map.MapActivity;
import com.hcmut.admin.utraffictest.util.SharedPrefUtils;

import java.util.Map;

import static com.hcmut.admin.utraffictest.util.TrafficNotificationFactory.DIRECTION_NOTIFICATION_ID;
import static com.hcmut.admin.utraffictest.util.TrafficNotificationFactory.NORMAL_NOTIFICATION_ID;


public class FirebaseCloudMessageService extends FirebaseMessagingService {
    AndroidExt androidExt = new AndroidExt();

    private static final String NOTI_TYPE_FEILD = "notiType";
    private static final String SEGMENTS_FEILD = "segments";
    private static final String REPORT_NOTI_TYPE = "REPORT_NOTI_TYPE";
    private static final String DIRECTION_NOTI_TYPE = "DIRECTION_NOTI_TYPE";

    // report field
    private static final String REPORT_ID_FIELD = "reportId";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String, String> remoteData = remoteMessage.getData();
        String notiType = remoteData.get(NOTI_TYPE_FEILD);
        String segments = remoteData.get(SEGMENTS_FEILD);
        if (segments == null) {
            return;
        }
        if (notiType == null) {
            return;
        }

        try {
            Gson gson = new Gson();
            SegmentNotificationDataMessage[] segmentArray = gson.fromJson(segments, SegmentNotificationDataMessage[].class);
            if (segmentArray.length == 0) {
                return;
            }
            switch (notiType) {
                case REPORT_NOTI_TYPE:
                    pushReportNotification(segmentArray);
                    break;
                case DIRECTION_NOTI_TYPE:
                    pushDirectionNotification(segmentArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pushNotificationMessage() {
        TrafficNotificationFactory trafficNotificationFactory = TrafficNotificationFactory.getInstance(getApplicationContext());
        Notification notification = trafficNotificationFactory.getNotificationMessage(
                getApplicationContext(),
                MapActivity.class,
                "Notification message",
                "Have a message");
        trafficNotificationFactory.sendNotification(notification, NORMAL_NOTIFICATION_ID);
    }

    private void pushDirectionNotification(SegmentNotificationDataMessage[] segmentArray) {
        TrafficNotificationFactory trafficNotificationFactory = TrafficNotificationFactory.getInstance(getApplicationContext());
        Notification notification = trafficNotificationFactory.getFoundNewWayNotification(
                getApplicationContext(), MapActivity.class);
        trafficNotificationFactory.sendNotification(notification, DIRECTION_NOTIFICATION_ID);
    }

    // TODO: start Detail Report Fragment
    private void pushReportNotification(SegmentNotificationDataMessage[] segmentArray) {
        String title = "Cảnh báo";
        Intent intent = new Intent(this, MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(title)
                        .setContentText("Có kẹt xe khu vực xung quanh 1km")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri);
        if (pendingIntent != null) {
            notificationBuilder.setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name_01";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }


        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("firebase token", s);
        SharedPrefUtils.saveNotiToken(getApplicationContext(), s);

    }
}

