package com.hcmut.admin.utrafficsystem.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hcmut.admin.utrafficsystem.business.GPSForegroundServiceHandler;

public class LocationWakefulReceiver extends BroadcastReceiver {
    public static long WAKEUP_DELAY_MILLIS = 60000 * 5;    // 5 minutes

    public static String WAKEUP_ID = "wake_up_id";
    public static int WAKEUP_LOCATION_SERVICE_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Wakeup", "Run");
        int wakeupId = intent.getIntExtra(WAKEUP_ID, 0);
        if (wakeupId == WAKEUP_LOCATION_SERVICE_ID) {
            try {
                GPSForegroundServiceHandler.startLocationService(context.getApplicationContext());
            } catch (Exception e) {
            }
        }
    }
}
