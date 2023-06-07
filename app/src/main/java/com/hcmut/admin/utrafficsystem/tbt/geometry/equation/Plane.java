package com.hcmut.admin.utrafficsystem.tbt.geometry.equation;

import com.hcmut.admin.utrafficsystem.tbt.geometry.Point;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Vector;

public class Plane {
    public final float a, b, c, d;

    public Plane(float a, float b, float c, float d) {
        this.a = a; this.b = b; this.c = c; this.d = d;
    }

    public Plane(Vector normal, Point point) {
        this.a = normal.x;
        this.b = normal.y;
        this.c = normal.z;
        this.d = -(normal.x * point.x + normal.y * point.y + normal.z * point.z);
    }
}
