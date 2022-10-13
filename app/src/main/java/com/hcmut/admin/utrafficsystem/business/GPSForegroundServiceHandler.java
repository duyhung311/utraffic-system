package com.hcmut.admin.utrafficsystem.business;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.hcmut.admin.utrafficsystem.service.AppForegroundService;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;

public class GPSForegroundServiceHandler {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 888;
    public static final int MUTILE_PERMISSION_REQUEST = 777;
    public static final int LOCATION_PERMISSION_REQUEST = 666;
    private static final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    private static final String[] locationPermissions = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    public GPSForegroundServiceHandler() {
    }

    public static boolean requireLocationPermission(Activity activity) {
        // check Location permission
        if (!hasPermisson(activity, locationPermissions)) {
            ActivityCompat.requestPermissions(activity, locationPermissions, LOCATION_PERMISSION_REQUEST);
            return false;
        }
        return true;
    }

    /**
     *
     */
    public static void initLocationService(MapActivity activity) {
        if (isServiceRunning(activity)) {
            return;
        }

        // check Google play service
        if (!isPlayServicesInstalled(activity)) {
            // notification to user about google play service is not installed
            Toast.makeText(activity, "Google play service is not installed", Toast.LENGTH_SHORT).show();
            return;
        }

        // check Location permission
        if (!hasPermisson(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, MUTILE_PERMISSION_REQUEST);
        } else {
            startLocationService(activity.getApplicationContext());
        }
    }

    public static boolean handleLocationPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            return (grantResults.length > 1) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED);
        }
        return false;
    }

    public static boolean handleAppForgroundPermission(MapActivity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MUTILE_PERMISSION_REQUEST) {
            if ((grantResults.length > 2) && (grantResults[0] + grantResults[1] + grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                startLocationService(activity.getApplicationContext());
            }
            return true;
        }
        return false;
    }

    private static boolean isServiceRunning(MapActivity activity) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (AppForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     *  @return:    true if google play service is installed else return false
     */
    private static boolean isPlayServicesInstalled(MapActivity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                activity.finish();
            }
            return false;
        }
        return true;
    }

    private static boolean hasPermisson(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }

    public static void startLocationService(Context context) {
        try {
            if (Build.VERSION.SDK_INT < 26) {
                context.startService(new Intent(context.getApplicationContext(), AppForegroundService.class));
            } else {
                context.startForegroundService(new Intent(context.getApplicationContext(), AppForegroundService.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopLocationService(Context context) {
        try {
            context.stopService(new Intent(context.getApplicationContext(), AppForegroundService.class));
        } catch (Exception e) {

        }
    }
}
