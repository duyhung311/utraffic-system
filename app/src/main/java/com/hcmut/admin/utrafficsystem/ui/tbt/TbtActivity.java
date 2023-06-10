package com.hcmut.admin.utrafficsystem.ui.tbt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.model.AndroidExt;
import com.hcmut.admin.utrafficsystem.tbt.TbtRenderer;
import com.hcmut.admin.utrafficsystem.tbt.utils.MultisampleConfigChooser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TbtActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FusedLocationProviderClient fusedLocationClient;
    private static final AndroidExt ANDROID_EXT = new AndroidExt();
    private GLSurfaceView mGLSurfaceView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Turn off the window's title bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.turn_by_turn);
        mGLSurfaceView = findViewById(R.id.glSurfaceView);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        Intent intent = getIntent();
        LatLng startPoint = intent.getParcelableExtra("startPoint");
        LatLng endPoint = intent.getParcelableExtra("endPoint");
        boolean isTimeDirectionSelected = intent.getBooleanExtra("isTimeDirectionSelected", false);


        Location source = new Location("") {{
            setLatitude(startPoint.latitude);
            setLongitude(startPoint.longitude);
        }};
        Location destination = new Location("") {{
            setLatitude(endPoint.latitude);
            setLongitude(endPoint.longitude);
        }};
//        final TbtRenderer tbtRenderer = new TbtRenderer(this, NavTest.coords[1], NavTest.coords[0], 106.65451236069202, 10.780349396189212);
        final TbtRenderer tbtRenderer = new TbtRenderer(this, source, destination, isTimeDirectionSelected);

        if (supportsEs2) {
            mGLSurfaceView.setEGLContextClientVersion(2);
            mGLSurfaceView.setEGLConfigChooser(new MultisampleConfigChooser());

            mGLSurfaceView.setRenderer(tbtRenderer);
        } else {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            Toast.makeText(this, "Thiết bị không hỗ trợ OpenGL ES 2.0.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        TextView distanceText = findViewById(R.id.turn_by_turn_distance);
        TextView timeText = findViewById(R.id.turn_by_turn_time);

        Locale locale = Locale.getDefault();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", locale);

        tbtRenderer.setOnRouteChanged((disInMeter, timeInMin) -> runOnUiThread(() -> {
            if (disInMeter == null || timeInMin == null) {
                distanceText.setText("Đang tìm...");
                timeText.setText("Đang tìm đường");
                return;
            }

            String distanceStr = String.format(locale, "%.1f km", disInMeter / 1000);
            distanceText.setText(distanceStr);
            int roundTime = Math.round(timeInMin);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, roundTime);
            String estTime = timeFormat.format(calendar.getTime());

            String timeStr = String.format(locale, "%d phút - %s", roundTime, estTime);
            timeText.setText(timeStr);
        }));

        tbtRenderer.setOnFinish(() -> runOnUiThread(() -> {
            distanceText.setText("Đã đến nơi");
            timeText.setText("Đã đến nơi");
            ANDROID_EXT.showNotifyDialog(this, "Đã đến nơi", this::finish);
        }));

        mGLSurfaceView.setOnTouchListener((v, event) -> {
            if (event != null) {
                final float eventX = event.getX();
                final float eventY = event.getY();
                final int eventType = event.getActionMasked();
                final int eventPointerCount = event.getPointerCount();
                if (eventPointerCount > 1) {
                    List<Float> eventXs = new ArrayList<>();
                    List<Float> eventYs = new ArrayList<>();
                    for (int i = 0; i < eventPointerCount; i++) {
                        eventXs.add(event.getX(i));
                        eventYs.add(event.getY(i));
                    }
                    if (eventType == MotionEvent.ACTION_POINTER_DOWN) {
                        mGLSurfaceView.queueEvent(() -> tbtRenderer.actionPointerDown(eventXs, eventYs));
                    } else if (eventType == MotionEvent.ACTION_MOVE) {
                        mGLSurfaceView.queueEvent(() -> tbtRenderer.actionPointerMove(eventXs, eventYs));
                    } else if (eventType == MotionEvent.ACTION_POINTER_UP) {
                        mGLSurfaceView.queueEvent(() -> tbtRenderer.actionPointerUp(eventXs, eventYs));
                    }

                } else if (eventType == MotionEvent.ACTION_DOWN) {
                    mGLSurfaceView.queueEvent(() -> tbtRenderer.actionDown(eventX, eventY));
                } else if (eventType == MotionEvent.ACTION_MOVE) {
                    mGLSurfaceView.queueEvent(() -> tbtRenderer.actionMove(eventX, eventY));
                } else if (eventType == MotionEvent.ACTION_UP) {
                    mGLSurfaceView.queueEvent(() -> tbtRenderer.actionUp(eventX, eventY));
                }
                return true;
            } else {
                return false;
            }
        });


        Button recenterBtn = findViewById(R.id.recenter_btn);
        recenterBtn.setOnClickListener(v -> mGLSurfaceView.queueEvent(tbtRenderer::recenter));

        Button exitBtn = findViewById(R.id.exit_btn);
        exitBtn.setOnClickListener(v -> finish());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLocations().get(0);
                tbtRenderer.changeLocation(location);
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    protected void onResume() {
        // The activity must call the GL surface view's onResume() on activity
        // onResume().
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        // mGLSurfaceView.onPause();
    }

}
