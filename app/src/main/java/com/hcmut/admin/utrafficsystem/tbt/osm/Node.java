package com.hcmut.admin.utrafficsystem.tbt.osm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmut.admin.utrafficsystem.tbt.algorithm.CoordinateTransform;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Point;

import org.osgeo.proj4j.ProjCoordinate;

public class Node {
    public final float lat;
    public final float lon;

    public Node(float lon, float lat, boolean isWebMercator) {
        if (isWebMercator) {
            this.lon = lon;
            this.lat = lat;
            return;
        }

        ProjCoordinate projCoordinate = CoordinateTransform.wgs84ToWebMercator(lat, lon);
        this.lon = (float) projCoordinate.x;
        this.lat = (float) projCoordinate.y;

        if (Float.isNaN(this.lon) || Float.isNaN(this.lat)) {
            System.err.println("Invalid node: " + this);
            System.err.println("\tOriginal: " + lon + ", " + lat);
        }
    }

    public Node(float lon, float lat) {
        this(lon, lat, false);
    }

    public Point toPoint() {
        return new Point(lon, lat);
    }

    public Point toPoint(float scale) {
        return new Point(lon * scale, lat * scale);
    }

    public Point toPoint(float originX, float originY, float scale) {
        return new Point((lon - originX) * scale, (lat - originY) * scale);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Node && ((Node) obj).lat == lat && ((Node) obj).lon == lon;
    }

    @NonNull
    @Override
    public String toString() {
        return "Node{" +
                "lon=" + lon +
                ", lat=" + lat +
                '}';
    }
}
