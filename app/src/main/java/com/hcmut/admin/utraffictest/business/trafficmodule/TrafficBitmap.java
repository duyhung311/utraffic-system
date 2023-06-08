package com.hcmut.admin.utraffictest.business.trafficmodule;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.geometry.Point;
import com.google.maps.android.projection.SphericalMercatorProjection;
import com.hcmut.admin.utraffictest.business.TileCoordinates;
import com.hcmut.admin.utraffictest.business.trafficmodule.groundoverlay.BitmapLineData;
import com.hcmut.admin.utraffictest.repository.local.room.entity.StatusRenderDataEntity;
import com.hcmut.admin.utraffictest.util.MyLatLngBoundsUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.annotation.Nullable;

public class TrafficBitmap {
    public static final int TILE_ZOOM_15_SCALE = 2;
    public static final int mTileSize = 256;
    private static final SphericalMercatorProjection mProjection = new SphericalMercatorProjection(mTileSize);
    private static final int DEFAULT_COLOR = Color.BLACK;

    public TrafficBitmap() {
    }

    /**
     * - Draw bitmap
     * - call invalidate() to render tile
     * - cache bitmap to Glide
     * @param tile
     * @param lineDataList
     */
    public  <T> Bitmap createTrafficBitmap(@NotNull TileCoordinates tile, @Nullable List<T> lineDataList, int mScale, Float lineWidth) {
        if (lineDataList == null || lineDataList.size() == 0) {
            return null;
        }
        Log.e("Tile", "render status, size " + lineDataList.size());
        int dimension = mScale * mTileSize;
        Matrix matrix = new Matrix();
        float scale = ((float) Math.pow(2, tile.z) * mScale / 10);
        matrix.postScale(scale, scale);
        matrix.postTranslate(-tile.x * dimension, -tile.y * dimension);

        Bitmap bitmap = Bitmap.createBitmap(dimension, dimension, Bitmap.Config.ARGB_8888); //save memory on old phones
        Canvas c = new Canvas(bitmap);
        c.setMatrix(matrix);
        c = drawCanvasFromArray(c, lineDataList, tile, lineWidth);
        return bitmap;
    }

    /**
     * Here the Canvas can be drawn on based on data provided from a Spherical Mercator Projection
     *
     * @param c
     * @param tile
     * @return
     */
    private <T> Canvas drawCanvasFromArray(Canvas c, @NotNull List<T> lineDataList, TileCoordinates tile, Float lineWidth) {
        //Line features
        Paint paint = new Paint();
        paint.setStrokeWidth((lineWidth == null) ? getLineWidth(tile.z) : lineWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(DEFAULT_COLOR);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setShadowLayer(0, 0, 0, 0);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setAlpha(getAlpha(tile.z));
        paint.setAntiAlias(true);

        T item = lineDataList.get(0);
        if (item instanceof BitmapLineData) {
            drawByBitmapLineData(c, paint, (List<BitmapLineData>) lineDataList);
        } else if (item instanceof StatusRenderDataEntity) {
            drawByRenderDataEntity(c, paint, (List<StatusRenderDataEntity>) lineDataList, tile);
        }
        return c;
    }

    private void drawByBitmapLineData(Canvas c, Paint paint, List<BitmapLineData> lineDataList) {
        if (lineDataList != null) {
            float startX;
            float startY;
            float stopX;
            float stopY;
            for (BitmapLineData lineData : lineDataList) {
                // start point
                Point screenPt1 = mProjection.toPoint(lineData.startLatLng);
                startX = (float) screenPt1.x * 10;
                startY = (float) screenPt1.y * 10;

                // stop point
                Point screenPt2 = mProjection.toPoint(lineData.endLatLng);
                stopX = (float) screenPt2.x * 10;
                stopY = (float) screenPt2.y * 10;

                // draw polyline
                paint.setColor(Color.parseColor(lineData.color));
                c.drawLine(startX, startY, stopX, stopY, paint);
            }
        }
    }

    private void drawByRenderDataEntity(Canvas c, Paint paint, List<StatusRenderDataEntity> lineDataList, TileCoordinates tile) {
        if (lineDataList != null) {
            float startX;
            float startY;
            float stopX;
            float stopY;
            float middleX;
            float middleY;
            LatLngBounds bounds = MyLatLngBoundsUtil.tileToLatLngBound(tile);
            LatLng startPoint;
            LatLng endPoint;
            for (StatusRenderDataEntity lineData : lineDataList) {
                // start point
                startPoint = lineData.getStartLatlng();
                Point screenPt1 = mProjection.toPoint(startPoint);
                startX = (float) screenPt1.x * 10;
                startY = (float) screenPt1.y * 10;

                // stop point
                endPoint = lineData.getEndLatlng();
                Point screenPt2 = mProjection.toPoint(endPoint);
                stopX = (float) screenPt2.x * 10;
                stopY = (float) screenPt2.y * 10;

                // draw polyline
                paint.setColor(Color.parseColor(lineData.color));
                if (bounds.contains(startPoint)) {
                    c.drawLine(startX, startY, stopX, stopY, paint);
                } else if (bounds.contains(endPoint)) {
                    c.drawLine(stopX, stopY, startX, startY, paint);
                } else {
                    // middle point
                    Point screenPt3 = MyLatLngBoundsUtil.getMiddlePoint(screenPt1, screenPt2);
                    middleX = (float) screenPt3.x * 10;
                    middleY = (float) screenPt3.y * 10;

                    c.drawLine(middleX, middleY, startX, startY, paint);
                    c.drawLine(middleX, middleY, stopX, stopY, paint);
                }
            }
        }
    }

    /**
     * This will let you adjust the line width based on zoom level
     *
     * @param zoom
     * @return
     */
    private float getLineWidth(int zoom) {
        Log.e("zoom", "" + zoom);
        switch (zoom) {
            case 21:
            case 20:
            case 19:
                return 0.00015f;
            case 18:
                return 0.00015f;//ok
            case 17:
                return 0.00013f; //ok
            case 16:
                return 0.0002f; //ok
            case 15:
            case 14:
                return 0.0005f;
            case 13:
                return 0.0015f;
            case 12:
                return 0.003f;
            case 11:
                return 0.004f;
            case 10:
                return 0.0048f;
            case 9:
            case 8:
                return 0.0058f;
            default:
                return 0.0022f;
        }
    }

    /**
     * This will let you adjust the alpha value based on zoom level
     *
     * @param zoom
     * @return
     */
    private int getAlpha(int zoom) {

        switch (zoom) {
            case 20:
                return 140;
            case 19:
                return 140;
            case 18:
                return 140;
            case 17:
                return 140;
            case 16:
                return 180;
            case 15:
                return 180;
            case 14:
                return 180;
            default:
                return 255;
        }
    }
}
