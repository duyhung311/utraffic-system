package com.hcmut.admin.utrafficsystem.tbt.geometry;

import androidx.annotation.NonNull;

public class BoundBox {
    public final float minX;
    public final float minY;
    public final float maxX;
    public final float maxY;

    public BoundBox(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public BoundBox(float[] points) {
        this(points[0], points[1], points[2], points[3]);
    }

    public boolean within(BoundBox boundBox) {
        return minX >= boundBox.minX && minY >= boundBox.minY && maxX <= boundBox.maxX && maxY <= boundBox.maxY;
    }

    public boolean intersects(BoundBox boundBox) {
        return !(minX > boundBox.maxX || maxX < boundBox.minX || minY > boundBox.maxY || maxY < boundBox.minY);
    }

    public boolean withinOrIntersects(BoundBox boundBox) {
        return intersects(boundBox) || within(boundBox) || boundBox.within(this);
    }

    public Point getCenter() {
        return new Point((minX + maxX) / 2, (minY + maxY) / 2);
    }

    public BoundBox scale(float scale) {
        Point center = getCenter();
        float width = maxX - minX;
        float height = maxY - minY;
        float newWidth = width * scale;
        float newHeight = height * scale;
        return new BoundBox(center.x - newWidth / 2, center.y - newHeight / 2, center.x + newWidth / 2, center.y + newHeight / 2);
    }

    public boolean contains(Point point) {
        return point.x >= minX && point.x <= maxX && point.y >= minY && point.y <= maxY;
    }

    public float getRadius() {
        Point center = getCenter();

        float dx = center.x - minX;
        float dy = center.y - minY;

        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    @NonNull
    @Override
    public String toString() {
        return "BoundBox{" +
                "minX=" + minX +
                ", minY=" + minY +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                '}';
    }
}
