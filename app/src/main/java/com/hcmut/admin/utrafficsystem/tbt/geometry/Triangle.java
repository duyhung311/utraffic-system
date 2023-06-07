package com.hcmut.admin.utrafficsystem.tbt.geometry;

import androidx.annotation.NonNull;

public class Triangle {
    public final Point p1;
    public final Point p2;
    public final Point p3;

    public Triangle(Point p1, Point p2, Point p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public Triangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3) {
        this.p1 = new Point(x1, y1, z1);
        this.p2 = new Point(x2, y2, z2);
        this.p3 = new Point(x3, y3, z3);
    }

    public float getArea() {
        Vector v1 = new Vector(p1, p2);
        Vector v2 = new Vector(p1, p3);
        return v1.cross(v2).length() / 2;
    }

    public Point getCentroid() {
        return new Point((p1.x + p2.x + p3.x) / 3, (p1.y + p2.y + p3.y) / 3, (p1.z + p2.z + p3.z) / 3);
    }

    @NonNull
    @Override
    public String toString() {
        return "Triangle{" +
                "p1=" + p1 +
                ", p2=" + p2 +
                ", p3=" + p3 +
                '}';
    }
}
