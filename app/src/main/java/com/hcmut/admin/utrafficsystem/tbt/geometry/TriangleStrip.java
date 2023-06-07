package com.hcmut.admin.utrafficsystem.tbt.geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TriangleStrip extends PointList {
    private final List<Integer> indices = new ArrayList<>();

    public TriangleStrip(List<Point> points) {
        super(points);
        assert points.size() >= 3: "Triangle strip must have at least 3 points";
    }

    public TriangleStrip(Point[] points) {
        this(Arrays.asList(points));
    }

    public TriangleStrip(Point p1, Point p2, Point p3) {
        this(List.of(p1, p2, p3));
    }

    public TriangleStrip(float[] points) {
        this(new ArrayList<>() {
            {
                for (int i = 0; i < points.length; i += 3) {
                    add(new Point(points[i], points[i + 1], points[i + 2]));
                }
            }
        });
    }

    public TriangleStrip(TriangleStrip strip) {
        this(strip.points);
    }

    public TriangleStrip add(Point p) {
        this.points.add(p);
        return this;
    }

    public TriangleStrip add(Point[] points) {
        this.points.addAll(List.of(points));
        return this;
    }

    public TriangleStrip add(List<Point> points) {
        this.points.addAll(points);
        return this;
    }

    public void extend(TriangleStrip strip) {
        if (points.size() > 0) {
            indices.add(points.size() - 1);
            this.points.add(points.get(points.size() - 1));
            this.points.add(strip.points.get(0));
        }
        this.points.addAll(strip.points);
    }
}
