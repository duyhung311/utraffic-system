package com.hcmut.admin.utrafficsystem.tbt.geometry;

import java.util.ArrayList;
import java.util.List;

public class PointList {
    public final List<Point> points;

    public PointList(List<Point> points) {
        this.points = new ArrayList<>(points);
    }

    public PointList(PointList pointList) {
        this(pointList.points);
    }

    public boolean isClosed() {
        return points.size() > 0 && points.get(0).equals(points.get(points.size() - 1));
    }
}
