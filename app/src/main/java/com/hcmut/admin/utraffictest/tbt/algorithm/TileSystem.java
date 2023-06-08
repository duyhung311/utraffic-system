package com.hcmut.admin.utraffictest.tbt.algorithm;

public class TileSystem {
    public static final int ZOOM = 17;
    private static final long TWO_POWER_ZOOM = 1L << ZOOM;

    public static long getTileId(double lon, double lat, int offsetX, int offsetY) {
        long tileX = (long) Math.floor(((lon + 180) / 360 * TWO_POWER_ZOOM)) + offsetX;
        long tileY = (long) Math.floor(((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * TWO_POWER_ZOOM)) + offsetY;
        return (tileY << ZOOM) + tileX;
    }

    public static long getTileId(double lon, double lat) {
        return getTileId(lon, lat, 0, 0);
    }

    public static long getTileId(long tileId, int offsetX, int offsetY) {
        if (offsetX == 0 && offsetY == 0) {
            return tileId;
        }
        long tileX = (tileId & ((1 << ZOOM) - 1)) + offsetX;
        long tileY = (tileId >> ZOOM) + offsetY;
        return (tileY << ZOOM) + tileX;
    }

    public static float[] getBoundBox(long tileId, int offsetX, int offsetY) {
        long tileX = (tileId & ((1 << ZOOM) - 1)) + offsetX;
        long tileY = (tileId >> ZOOM) + offsetY;

        float minLon = (float) (tileX / (double) TWO_POWER_ZOOM * 360.0 - 180);
        float minLat = (float) (Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1 - 2 * (tileY + 1) / (double) TWO_POWER_ZOOM)))));
        float maxLon = (float) ((tileX + 1) / (double) TWO_POWER_ZOOM * 360.0 - 180);
        float maxLat = (float) (Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1 - 2 * tileY / (double) TWO_POWER_ZOOM)))));

        return new float[]{minLon, minLat, maxLon, maxLat};
    }

    public static float[] getBoundBox(long tileId) {
        return getBoundBox(tileId, 0, 0);
    }
}
