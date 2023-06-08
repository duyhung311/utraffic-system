package com.hcmut.admin.utraffictest.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.geometry.Point;
import com.hcmut.admin.utraffictest.business.TileCoordinates;

import org.jetbrains.annotations.NotNull;

public class MyLatLngBoundsUtil {
    public static LatLngBounds tileToLatLngBound(TileCoordinates tileCoordinates) {
        final int x = tileCoordinates.x;
        final int y = tileCoordinates.y;
        final int zoom = tileCoordinates.z;
        double north = tile2lat(y, zoom);
        double south = tile2lat(y + 1, zoom);
        double west = tile2lon(x, zoom);
        double east = tile2lon(x + 1, zoom);
        LatLng southwest = new LatLng(south, west);
        LatLng northeast = new LatLng(north, east);
        return new LatLngBounds(southwest, northeast);
    }

    private static double tile2lon(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    private static double tile2lat(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }

    /**
     *
     * @param lat
     * @param lon
     * @param zoom: zoom of tile which want to get
     * @return: tile with zoom level contain latlng
     */
    public static TileCoordinates getTileNumber(final double lat, final double lon, final int zoom) throws TileCoordinates.TileCoordinatesNotValid {
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
        return TileCoordinates.getTileCoordinates(xtile, ytile, zoom);
    }

    public static TileCoordinates convertTile(TileCoordinates source, int zoom) {
        if (source.z == zoom) {
            return source;
        }
        LatLng center = MyLatLngBoundsUtil.tileToLatLngBound(source).getCenter();
        try {
            return MyLatLngBoundsUtil.getTileNumber(center.latitude, center.longitude, zoom);
        } catch (TileCoordinates.TileCoordinatesNotValid tileCoordinatesNotValid) {
        }
        return null;
    }

    @Deprecated
    public static LatLng getMiddlePoint(LatLng latLng1, LatLng latLng2){
        double lat1 = latLng1.latitude;
        double lon1 = latLng1.longitude;
        double lat2 = latLng2.latitude;
        double lon2 = latLng2.longitude;
        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        //print out in degrees
        return new LatLng(lat3, lon3);
    }

    public static Point getMiddlePoint (@NotNull Point point1, @NotNull Point point2) {
        return new Point(((point1.x + point2.x) / 2), ((point1.y + point2.y) / 2));
    }
}
