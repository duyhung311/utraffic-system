package com.hcmut.admin.utrafficsystem.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class Compass implements SensorEventListener {
    private CompassListener listener;
    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor msensor;
    private Sensor rsensor;

    private float[] mRotation = new float[3];
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float[] mAccMagMatrix = new float[9];
    private float[] mRotationMatrixFromVector = new float[9];
    // Orientation angles from accelerometer and magnetometer
    private float[] mOrientation = new float[3];

    private static float preAzimuth;
    private boolean hasRotationSensor = true;


    public static float getAzimuth() {
        return preAzimuth;
    }

    public Compass(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        rsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        hasRotationSensor = (rsensor != null);
        if (gsensor == null || msensor == null) {
            Toast.makeText(context, "Sensors not available!", Toast.LENGTH_SHORT).show();
        }
    }

    public void start() {
        sensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, rsensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void setListener(CompassListener l) {
        listener = l;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.75f;

//        synchronized (this) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ROTATION_VECTOR:
                    System.arraycopy(event.values, 0, mRotation, 0, 3);
                    calculateOrientation();
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
                    mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
                    mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];
                    calculateOrientation();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0];
                    mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1];
                    mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2];
                    break;
            }
//        }
    }

    private void calculateOrientation() {
        // If phone have Rotation Vector sensor
        if (SensorManager.getRotationMatrix(mAccMagMatrix, null, mGravity, mGeomagnetic) && !hasRotationSensor) {
            SensorManager.getOrientation(mAccMagMatrix, mOrientation);
        } else {
            SensorManager.getRotationMatrixFromVector(mRotationMatrixFromVector, mRotation);
            SensorManager.getOrientation(mRotationMatrixFromVector, mOrientation);
        }
        // Calculate azimuth to detect direction
        float currentAzimuth = (float) Math.toDegrees(mOrientation[0]);
        currentAzimuth = currentAzimuth % 360;

        // Only notify other receivers if there is a change in orientation greater than 1.0 degrees
        int delta = (int) Math.abs(currentAzimuth - preAzimuth);
        float finalCurrentAzimuth = currentAzimuth;
        if (delta >= 1) {
            preAzimuth = finalCurrentAzimuth;
            if (listener != null) {
                listener.onNewAzimuth(finalCurrentAzimuth);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public interface CompassListener {
        void onNewAzimuth(float azimuth);
    }
}