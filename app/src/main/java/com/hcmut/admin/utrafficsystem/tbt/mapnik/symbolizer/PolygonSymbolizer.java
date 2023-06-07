package com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer;

import android.opengl.GLES20;

import androidx.annotation.NonNull;

import com.hcmut.admin.utrafficsystem.tbt.algorithm.CoordinateTransform;
import com.hcmut.admin.utrafficsystem.tbt.data.VertexArray;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Polygon;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Triangle;
import com.hcmut.admin.utrafficsystem.tbt.osm.Way;
import com.hcmut.admin.utrafficsystem.tbt.programs.ColorShaderProgram;
import com.hcmut.admin.utrafficsystem.tbt.utils.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// PolygonSymbolizer keys: [fill, fill-opacity, gamma, clip]
public class PolygonSymbolizer extends Symbolizer {
    static class PolygonSymMeta extends SymMeta {
        final List<float[]> drawables;
        protected VertexArray vertexArray = null;

        public PolygonSymMeta() {
            this.drawables = new ArrayList<>(0);
        }

        public PolygonSymMeta(float[] drawable) {
            this.drawables = new ArrayList<>(1) {{
                add(drawable);
            }};
        }

        public PolygonSymMeta(List<float[]> drawables) {
            this.drawables = drawables;
        }

        @Override
        public boolean isEmpty() {
            return vertexArray == null && drawables.isEmpty();
        }

        @Override
        public SymMeta append(SymMeta other) {
            if (!(other instanceof PolygonSymMeta)) return this;
            PolygonSymMeta otherLineSymMeta = (PolygonSymMeta) other;
            List<float[]> result = new ArrayList<>(drawables);
            result.addAll(otherLineSymMeta.drawables);
            return new PolygonSymMeta(result);
        }

        private void draw(ColorShaderProgram shaderProgram) {
            if (isEmpty()) return;
            if (vertexArray == null) {
                float[] drawable = appendRegular(drawables);
                vertexArray = new VertexArray(shaderProgram, drawable);
            }
            shaderProgram.useProgram();
            vertexArray.setDataFromVertexData();
            int pointCount = vertexArray.getVertexCount();
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, pointCount);
        }

        @Override
        public void save(Config config) {
        }

        @Override
        public void draw() {
        }
    }

    private final float[] fillColor;

    public PolygonSymbolizer(Config config, String fill, String fillOpacity) {
        super(config);
        this.fillColor = parseColorString(fill, fillOpacity == null ? 1 : Float.parseFloat(fillOpacity));
    }

    @Override
    public SymMeta toDrawable(Way way, String layerName) {
        if (!way.isClosed()) return new PolygonSymMeta();
        float scaledPixel = CoordinateTransform.getScalePixel(config.getScaleDenominator());
        Polygon polygon = way.toPolygon(config.getOriginX(), config.getOriginY(), scaledPixel * config.getLengthPerPixel());
        List<Triangle> curTriangulatedTriangles = polygon.triangulate();
        return new PolygonSymMeta(ColorShaderProgram.toVertexData(new ArrayList<>() {
            {
                for (Triangle triangle : curTriangulatedTriangles) {
                    add(triangle.p1);
                    add(triangle.p2);
                    add(triangle.p3);
                }
            }
        }, fillColor));
    }

    @NonNull
    @Override
    public String toString() {
        return "<PolygonSymbolizer fill=\"" + Arrays.toString(fillColor);
    }
}
