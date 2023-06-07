package com.hcmut.admin.utrafficsystem.tbt.geometry.equation;

import com.hcmut.admin.utrafficsystem.tbt.geometry.Point;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Vector;

public class LineEquation3D {
    public final Point point;
    public final Vector direction;

    public LineEquation3D(Point point, Vector direction) {
        this.point = point;
        this.direction = direction;
    }

    public LineEquation3D(Point point, Point anotherPoint) {
        this.point = point;
        this.direction = new Vector(point, anotherPoint);
    }

    public Point getPointAtParameter(float t) {
        return point.add(direction.mul(t));
    }

    public Point intersectPlane(Plane plane) {
        // Check if the line is parallel to the plane
        float denominator = plane.a * direction.x + plane.b * direction.y + plane.c * direction.z;
        if (denominator == 0) {
            return null; // Line is parallel to the plane, no intersection.
        }

        // Calculate t at the intersection point.
        float t = -(plane.a * point.x + plane.b * point.y + plane.c * point.z + plane.d) / denominator;

        // Return the intersection point.
        return getPointAtParameter(t);
    }
}
