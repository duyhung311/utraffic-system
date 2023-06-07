package com.hcmut.admin.utrafficsystem.tbt.geometry.equation;

import androidx.annotation.NonNull;

import com.hcmut.admin.utrafficsystem.tbt.geometry.Point;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Vector;

public class LineEquation {
    // ax + by + c = 0
    public final float a, b, c;
    private static final double EPSILON = 1e-6;

    public LineEquation(float a, float b, float c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public LineEquation(Point p1, Point p2) {
        this.a = p2.y - p1.y;
        this.b = p1.x - p2.x;
        this.c = p2.x * p1.y - p1.x * p2.y;
    }

    public LineEquation(Vector v, Point p, boolean isOrthogonal) {
        if (isOrthogonal) {
            this.a = v.y;
            this.b = -v.x;
        } else {
            this.a = -v.y;
            this.b = v.x;
        }
        this.c = -this.a * p.x - this.b * p.y;
    }

    public boolean isSameSide(Point p1, Point p2) {
        float d1 = a * p1.x + b * p1.y + c;
        float d2 = a * p2.x + b * p2.y + c;
        return d1 * d2 > 0;
    }

    public boolean hasPoint(Point p) {
        float rv = a * p.x + b * p.y + c;
        return Math.abs(rv) < EPSILON;
    }

    public static Point intersect(LineEquation line1, LineEquation line2) {
        float det = line1.a * line2.b - line2.a * line1.b;
        if (Math.abs(det) < EPSILON) {
            return null;
        }
        float x = (line1.b * line2.c - line2.b * line1.c) / det;
        float y = (line2.a * line1.c - line1.a * line2.c) / det;
        return new Point(x, y);
    }

    @NonNull
    @Override
    public String toString() {
        return "LineEquation{" + a + "x + " + b + "y + " + c + " = 0}";
    }

    public float getTan() {
        return -a / b;
    }

    public float getArbitrary() {
        return -c / b;
    }
}
