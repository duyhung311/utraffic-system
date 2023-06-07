package com.hcmut.admin.utrafficsystem.tbt.algorithm;

import android.annotation.SuppressLint;

import java.util.List;
import java.util.function.BiFunction;

@SuppressLint("NewApi")
public class BezierCurveAnimation<T> {
    // Control points for cubic Bezier curve
    private static final double P0 = 0.0;
    private static final double P1 = 0.0;
    private static final double P2 = 0.58;
    private static final double P3 = 1.0;
    // Duration of the animation in nanoseconds
    private static final float DURATION = 0.5f * 1_000_000_000; // seconds * 1 billion
    private final BiFunction<List<T>, Float, T> scale;
    private long startTime = -1;
    private T minValue;
    private T maxValue;

    public BezierCurveAnimation(BiFunction<List<T>, Float, T> scale) {
        this.scale = scale;
    }
    // Cubic Bezier function
    private static double bezier(double t) {
        return Math.pow(1 - t, 3) * P0 + 3 * Math.pow(1 - t, 2) * t * P1 + 3 * (1 - t) * Math.pow(t, 2) * P2 + Math.pow(t, 3) * P3;
    }

    public void start(T minValue, T maxValue) {
        this.minValue = startTime == -1 ? minValue : getCurrent();
        this.maxValue = maxValue;
        startTime = System.nanoTime();
    }

    public boolean isRunning() {
        return startTime != -1;
    }

    public T getCurrent() {
        if (startTime == -1) {
            return minValue;
        }

        // Compute elapsed time, and then current time (t) in the range 0 to 1
        double elapsedTime = System.nanoTime() - startTime;

        if (elapsedTime >= DURATION) {
            startTime = -1;
            return maxValue;
        }

        double t = elapsedTime / DURATION;

        // Compute position on Bezier curve
        float position = (float) bezier(t);

        // Scale position between minValue and maxValue
        return scale.apply(List.of(minValue, maxValue), position);
    }

    public T getMinValue() {
        return minValue;
    }

    public T getMaxValue() {
        return maxValue;
    }
}
