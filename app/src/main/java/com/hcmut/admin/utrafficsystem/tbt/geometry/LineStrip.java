package com.hcmut.admin.utrafficsystem.tbt.geometry;

import java.util.ArrayList;
import java.util.List;

public class LineStrip extends PointList {
    public LineStrip(List<Point> points) {
        super(points);
        checkInit();
    }

    public LineStrip(PointList points) {
        this(points.points);
    }

    public LineStrip(float[] points) {
        this(new ArrayList<>() {
            {
                for (int i = 0; i < points.length; i += 3) {
                    add(new Point(points[i], points[i + 1], points[i + 2]));
                }
            }
        });
    }

    private void checkInit() {
//        assert points.size() >= 2 : "LineStrip must have at least 2 points";
    }

    public boolean isClosed() {
        return points.size() > 0 && points.get(0).equals(points.get(points.size() - 1));
    }

    public float getLength() {
        float length = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            length += points.get(i).distance(points.get(i + 1));
        }
        return length;
    }
}
