package com.hcmut.admin.utrafficsystem.tbt.geometry;

import androidx.annotation.NonNull;

public class Vector {
    public final float x;
    public final float y;
    public final float z;

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    public Vector() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector(Point p1, Point p2) {
        this.x = p2.x - p1.x;
        this.y = p2.y - p1.y;
        this.z = p2.z - p1.z;
    }

    public Vector(Point p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }

    public Vector(Vector v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public Vector add(Vector v) {
        return new Vector(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public Vector sub(Vector v) {
        return new Vector(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    public Vector mul(float k) {
        return new Vector(this.x * k, this.y * k, this.z * k);
    }

    public Vector div(float k) {
        return new Vector(this.x / k, this.y / k, this.z / k);
    }

    public float dot(Vector v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public Vector cross(Vector v) {
        return new Vector(
                this.y * v.z - this.z * v.y,
                this.z * v.x - this.x * v.z,
                this.x * v.y - this.y * v.x
        );
    }

    public float length() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Vector normalize() {
        float length = this.length();
        return new Vector(this.x / length, this.y / length, this.z / length);
    }

    public Vector rotate(float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        return new Vector(
                this.x * cos - this.y * sin,
                this.x * sin + this.y * cos,
                this.z
        );
    }

    public Vector rotateDeg(float angle) {
        return this.rotate(angle * (float) Math.PI / 180);
    }

    public Vector orthogonal2d() {
        return new Vector(-this.y, this.x, this.z);
    }

    public Vector negate() {
        return new Vector(-this.x, -this.y, -this.z);
    }

    public float angle(Vector v) {
        return (float) Math.acos(this.dot(v) / (this.length() * v.length()));
    }

    public float signedAngle(Vector v) {
        float angle = (float) Math.acos(this.dot(v) / (this.length() * v.length()));
        Vector cross = this.cross(v);
        if (cross.z < 0) {
            angle = -angle; // Angle is negative if cross product points down (in the -z direction)
        }
        return angle;
    }

    public boolean isInBetween(Vector v1, Vector v2) {
        return v1.cross(this).z * v1.cross(v2).z >= 0 && v2.cross(this).z * v2.cross(v1).z >= 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
