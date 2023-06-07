package com.hcmut.admin.utrafficsystem.tbt.geometry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Point {
    public final float x;
    public final float y;
    public final float z;

    public Point(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    public Point() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }

    public float distance(Point p) {
        return (float) Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2) + Math.pow(p.z - z, 2));
    }

    public Point midPoint(Point p) {
        return new Point((x + p.x) / 2, (y + p.y) / 2, (z + p.z) / 2);
    }

    public Point scale(float scale) {
        return new Point(x * scale, y * scale, z * scale);
    }

    public Point rotateDeg(float angle) {
        double rad = Math.toRadians(angle);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        return new Point(x * cos - y * sin, x * sin + y * cos, z);
    }

    public Point transform(float originX, float originY, float scale) {
        return new Point((x - originX) * scale, (y - originY) * scale, z);
    }

    public Point transform(float scaleX, float scaleY, float rotate, float translateX, float translateY) {
        // Apply scaling
        float x = this.x * scaleX;
        float y = this.y * scaleY;

        // Apply rotation
        double angle = Math.toRadians(rotate);
        float rotatedX = (float) (x * Math.cos(angle) - y * Math.sin(angle));
        float rotatedY = (float) (x * Math.sin(angle) + y * Math.cos(angle));
        x = rotatedX;
        y = rotatedY;

        // Apply translation
        x += translateX;
        y += translateY;

        // Return the transformed point
        return new Point(x, y);
    }

    public Point add(Vector v) {
        return new Point(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public Point add(Point p) {
        return new Point(this.x + p.x, this.y + p.y, this.z + p.z);
    }

    public static List<Point> toPoints(float[] points) {
        return new ArrayList<>() {
            {
                for (int i = 0; i < points.length; i += 3) {
                    add(new Point(points[i], points[i + 1], points[i + 2]));
                }
            }
        };
    }

    @NonNull
    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Point && ((Point) obj).x == x && ((Point) obj).y == y && ((Point) obj).z == z;
    }
}
