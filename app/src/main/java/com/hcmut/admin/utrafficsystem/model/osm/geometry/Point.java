package com.hcmut.admin.utrafficsystem.model.osm.geometry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmut.admin.utrafficsystem.model.osm.data.VertexData;

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

    public Point add(Vector v) {
        return new Point(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public float[] toVertexData() {
        return VertexData.toVertexData(this, true);
    }

    public float[] toVertexData(float r, float g, float b, float a) {
        return VertexData.toVertexData(this, r, g, b, a);
    }

    public static List<Point> toPoints(float[] points) {
        return new ArrayList<Point>() {
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
