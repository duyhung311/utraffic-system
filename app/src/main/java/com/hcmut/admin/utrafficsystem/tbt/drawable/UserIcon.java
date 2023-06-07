package com.hcmut.admin.utrafficsystem.tbt.drawable;

import android.location.Location;
import android.opengl.GLES20;

import com.hcmut.admin.utrafficsystem.tbt.algorithm.CoordinateTransform;
import com.hcmut.admin.utrafficsystem.tbt.data.VertexArray;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Point;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Vector;
import com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer.SymMeta;
import com.hcmut.admin.utrafficsystem.tbt.programs.ColorShaderProgram;
import com.hcmut.admin.utrafficsystem.tbt.utils.Config;

import org.osgeo.proj4j.ProjCoordinate;

import java.util.ArrayList;
import java.util.List;

public class UserIcon {
    private static final float RADIUS = 0.08f;
    private static final float RADIUS2 = 0.09f;
    private static final int NUM_OF_VERTICES = 50;
    private static final float[] COLOR_1 = new float[]{1, 1, 1, 1};
    private static final float[] COLOR_2 = new float[]{0, 0, 0, 1};
    private static final float[] ARROW_COLOR = new float[]{0.0f, 1f, 0.0f, 1.0f};
    private static final int NUM_OF_ARROW_VERTICES = 3;
    private final Config config;
    VertexArray vertexArray = null;
    private float[] drawable;

    public void relocate(Location location) {
        float scaled = CoordinateTransform.getScalePixel(config.getScaleDenominator()) * config.getLengthPerPixel();
        ProjCoordinate p = CoordinateTransform.wgs84ToWebMercator(location.getLatitude(), location.getLongitude());
        Point loc = new Point((float) p.x, (float) p.y).transform(config.getOriginX(), config.getOriginY(), scaled);
        Vector toLocation = new Vector(loc.x, loc.y);
        float dir = -location.getBearing();

        List<Point> points = new ArrayList<>(NUM_OF_VERTICES + 2);
        List<Point> points2 = new ArrayList<>(NUM_OF_VERTICES + 2);
        Point o = new Point(loc.x, loc.y);
        points.add(o);
        points2.add(o);
        for (int i = 0; i < NUM_OF_VERTICES; i++) {
            float angle = (float) (2 * Math.PI * i / NUM_OF_VERTICES);
            float x = loc.x + (float) (RADIUS * Math.cos(angle));
            float y = loc.y + (float) (RADIUS * Math.sin(angle));
            points.add(new Point(x, y));
            x = loc.x + (float) (RADIUS2 * Math.cos(angle));
            y = loc.y + (float) (RADIUS2 * Math.sin(angle));
            points2.add(new Point(x, y));
        }
        points.add(points.get(1));
        points2.add(points2.get(1));

        List<Point> arrowPoints = new ArrayList<>(NUM_OF_ARROW_VERTICES);
        arrowPoints.add(new Point(0, 0 + RADIUS).rotateDeg(dir).add(toLocation));
        arrowPoints.add(new Point(0 - RADIUS2 / 2, 0 - RADIUS2 / 2).rotateDeg(dir).add(toLocation));
        arrowPoints.add(new Point(0 + RADIUS2 / 2, 0 - RADIUS2 / 2).rotateDeg(dir).add(toLocation));

        float[] drawable = SymMeta.appendRegular(ColorShaderProgram.toVertexData(points, COLOR_1), ColorShaderProgram.toVertexData(points2, COLOR_2));
        drawable = SymMeta.appendRegular(drawable, ColorShaderProgram.toVertexData(arrowPoints, ARROW_COLOR));

        if (vertexArray == null) {
            this.drawable = drawable;
        } else {
            vertexArray.changeData(drawable);
        }
    }

    public UserIcon(Config config) {
        this.config = config;
        relocate(new Location(""));
    }

    public void draw() {
        if (vertexArray == null) {
            vertexArray = new VertexArray(config.getColorShaderProgram(), drawable, GLES20.GL_DYNAMIC_DRAW);
        }
        vertexArray.setDataFromVertexData();
        int pointCount = vertexArray.getVertexCount() - NUM_OF_ARROW_VERTICES;
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, pointCount / 2, pointCount / 2);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, pointCount / 2);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, pointCount, NUM_OF_ARROW_VERTICES);
    }
}
