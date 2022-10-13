package com.hcmut.admin.utrafficsystem.ui.setting;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.service.AppForegroundService;
import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.utrafficsystem.business.GPSForegroundServiceHandler;
import com.hcmut.admin.utrafficsystem.util.MapUtil;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SettingReferenceFragment extends PreferenceFragmentCompat {
    SwitchPreference swGpsCollectionRef;
    SwitchPreference swAutoSleepWakeupRef;
    SwitchPreference swNotifyAroundRef;
    SwitchPreference swNotifyMoveRef;

    public static final int REQUEST_CODE_PERMISSIONS = 101;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_reference, rootKey);

        swGpsCollectionRef = findPreference(getResources().getString(R.string.swGpsCollectionRef));
        swAutoSleepWakeupRef = findPreference(getResources().getString(R.string.swAutoSleepWakeupRef));
        swNotifyAroundRef = findPreference(getResources().getString(R.string.swNotifyAroundRef));
        swNotifyMoveRef = findPreference(getResources().getString(R.string.swNotifyMoveRef));
    }

    private void setEnableNotificationOption(boolean value) {
        swNotifyAroundRef.setEnabled(value);
        swNotifyMoveRef.setEnabled(value);
    }

    private void requestLocationPermission() {

        boolean foreground = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (foreground) {
            boolean background = ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

            if (!background) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE_PERMISSIONS);
            }
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    //foreground permission allowed
                    if (grantResults[i] >= 0) {
                        Toast.makeText(getApplicationContext(), "Foreground location permission allowed", Toast.LENGTH_SHORT).show();
                        continue;
                    } else {
                        Toast.makeText(getApplicationContext(), "Location Permission denied", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Context activity = view.getContext();
        if (activity instanceof MapActivity) {
            if (swGpsCollectionRef != null) {
                swGpsCollectionRef.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        MapActivity mapActivity = (MapActivity) activity;
                        if ("true".equals(newValue.toString())) {
                            requestLocationPermission();
                            if (MapUtil.checkGPSTurnOn(mapActivity, MapActivity.androidExt)) {
                                GPSForegroundServiceHandler.initLocationService(mapActivity);
                                setEnableNotificationOption(true);
                                return true;
                            }
                            return false;
                        }
                        GPSForegroundServiceHandler.stopLocationService(getContext());
                        setEnableNotificationOption(false);
                        return true;
                    }
                });
            }
        }

        if (swAutoSleepWakeupRef != null) {
            swAutoSleepWakeupRef.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    AppForegroundService.toggleSleepWakeup("true".equals(newValue.toString()), view.getContext());
                    return true;
                }
            });
        }
        if (swNotifyAroundRef != null) {
            swNotifyAroundRef.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    AppForegroundService.toggleReportNoti("true".equals(newValue.toString()), view.getContext());
                    return true;
                }
            });
        }
        if (swNotifyMoveRef != null) {
            swNotifyMoveRef.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    AppForegroundService.toggleDirectionNoti("true".equals(newValue.toString()), view.getContext());
                    return true;
                }
            });
        }

        if (swGpsCollectionRef != null && !swGpsCollectionRef.isChecked()) {
            setEnableNotificationOption(false);
        }
    }
}
