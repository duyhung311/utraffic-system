package com.hcmut.admin.utraffictest.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.hcmut.admin.utraffictest.R;
import com.hcmut.admin.utraffictest.service.AppForegroundService;

public class TrafficNotificationFactory {

    private String CHANNEL_ID = "Notification1";

    public static final int DIRECTION_NOTIFICATION_ID = 1;
    public static final int STOP_LOCATION_SERVICE_ALERT_NOTIFICATION_ID = 2;
    public static final int NORMAL_NOTIFICATION_ID = 1;
    public static final int STOPPED_NOTIFICATION_ID = 1;

    private static TrafficNotificationFactory serviceNotification;
    private Context context;

    private TrafficNotificationFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    public static TrafficNotificationFactory getInstance(Context context) {
        if (serviceNotification == null) {
            serviceNotification = new TrafficNotificationFactory(context);
            serviceNotification.createServiceNotificationChanel(context);
        }
        return serviceNotification;
    }

    public void createServiceNotificationChanel(Context context) {
        String CHANEL_NAME = "Chanel 1";
        String CHANEL_DISCRIPTION = "Day la channel 1";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANEL_NAME, android.app.NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANEL_DISCRIPTION);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public Notification getForegroundServiceNotification(Context context) {
        String STOP_ACTION_TITLE = "Dừng";
        String NOTIFICATION_CONTENT_TITLE = "Dữ liệu vị trí đang được thu thập";
        String NOTIFICATION_CONTENT_TEXT = "Để thông báo ùn tắc và gửi tình trạng giao thông.";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                .setContentTitle(NOTIFICATION_CONTENT_TITLE)
                .setContentText(NOTIFICATION_CONTENT_TEXT)
                .setContentInfo(NOTIFICATION_CONTENT_TEXT)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true);

        // create "stop" action button to stop foreground service
        Intent stopServiceIntent = new Intent(context.getApplicationContext(), AppForegroundService.class);
        stopServiceIntent.setAction(AppForegroundService.STOP_FOREGROUND_ACTION);
        PendingIntent cancelPendingIntent = PendingIntent.getService(
                context.getApplicationContext(),
                AppForegroundService.SERVICE_STOP_REQUEST_CODE,
                stopServiceIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        mBuilder.addAction(R.drawable.ic_arrow_back, STOP_ACTION_TITLE, cancelPendingIntent);

        return mBuilder.build();
    }

    public Notification getFoundNewWayNotification(Context context, Class activity) {
        // config string
        String NOTIFICATION_CONTENT_TITLE = "Cảnh báo";
        String NOTIFICATION_CONTENT_TEXT = "Xảy ra ùn tắc ở đoạn đường phía trước";

        //sound
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // pandingIntent for touch notification
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                .setContentTitle(NOTIFICATION_CONTENT_TITLE)
                .setContentText(NOTIFICATION_CONTENT_TEXT)
                .setContentIntent(pendingIntent)
                .setSound(defaultSound)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setOngoing(true);

        return mBuilder.build();
    }

    public Notification getNotificationMessage(Context context, Class activity, String title, String contentText) {
        //sound
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // pandingIntent for touch notification
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                .setContentTitle(title)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setSound(defaultSound)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setOngoing(true);

        return mBuilder.build();
    }

    public Notification getStopLocationServiceNotification(Context context, Class<?> activity) {
        String title = "Dữ liệu GPS đang được thu thập";
        String contentText = "Chạm để tùy chọn thiết lập thu thập dữ liệu";

        //sound
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // pandingIntent for touch notification
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                .setContentTitle(title)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setSound(defaultSound)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setOngoing(true);

        return mBuilder.build();
    }

    public Notification getStoppedServiceNotification(Context context) {
        String NOTIFICATION_CONTENT_TITLE = "Thu thập dữ liệu";
        String NOTIFICATION_CONTENT_TEXT = "Thu thập dữ liệu đã tắt do người dùng không di chuyển ...";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                .setContentTitle(NOTIFICATION_CONTENT_TITLE)
                .setContentText(NOTIFICATION_CONTENT_TEXT)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        return mBuilder.build();
    }

    public void sendNotification(Notification notification, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notification);
        }
    }
}
