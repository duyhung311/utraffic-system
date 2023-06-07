package com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer;

import android.annotation.SuppressLint;
import android.opengl.GLES20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmut.admin.utrafficsystem.tbt.algorithm.CoordinateTransform;
import com.hcmut.admin.utrafficsystem.tbt.algorithm.StrokeGenerator;
import com.hcmut.admin.utrafficsystem.tbt.data.VertexArray;
import com.hcmut.admin.utrafficsystem.tbt.geometry.LineStrip;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Point;
import com.hcmut.admin.utrafficsystem.tbt.geometry.TriangleStrip;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Vector;
import com.hcmut.admin.utrafficsystem.tbt.osm.Way;
import com.hcmut.admin.utrafficsystem.tbt.programs.ColorShaderProgram;
import com.hcmut.admin.utrafficsystem.tbt.utils.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// LineSymbolizer keys: [stroke-opacity, offset, stroke-linejoin, stroke-dasharray, stroke-width, stroke, clip, stroke-linecap]
public class LineSymbolizer extends Symbolizer {
    static class LineSymMeta extends SymMeta {
        final List<float[]> drawables;
        protected VertexArray vertexArray = null;
        private ColorShaderProgram shaderProgram;

        public LineSymMeta() {
            this.drawables = new ArrayList<>(0);
        }

        public LineSymMeta(float[] drawable) {
            this.drawables = new ArrayList<>(1) {{
                add(drawable);
            }};
        }

        public LineSymMeta(List<float[]> drawables) {
            this.drawables = drawables;
        }

        @Override
        public boolean isEmpty() {
            return vertexArray == null && drawables.isEmpty();
        }

        @Override
        public SymMeta append(SymMeta other) {
            if (!(other instanceof LineSymMeta)) return this;
            LineSymMeta otherLineSymMeta = (LineSymMeta) other;
            List<float[]> result = new ArrayList<>(drawables);
            result.addAll(otherLineSymMeta.drawables);
            return new LineSymMeta(result);
        }

        private void draw(ColorShaderProgram shaderProgram) {
            if (isEmpty()) return;
            if (vertexArray == null) {
                float[] drawable = appendTriangleStrip(drawables, ColorShaderProgram.TOTAL_VERTEX_ATTRIB_COUNT);
                vertexArray = new VertexArray(shaderProgram, drawable);
            }
            shaderProgram.useProgram();
            vertexArray.setDataFromVertexData();
            int pointCount = vertexArray.getVertexCount();
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, pointCount);
        }

        @Override
        public void save(Config config) {
            shaderProgram = config.getColorShaderProgram();
        }

        @Override
        public void draw() {
            draw(shaderProgram);
        }
    }

    private final float strokeWidth;
    private final float[] strokeColor;
    private final List<Float> strokeDashArray;
    private final StrokeGenerator.StrokeLineCap strokeLineCap;
    private final StrokeGenerator.StrokeLineJoin strokeLineJoin;
    private final float offset;

    public LineSymbolizer(Config config, @Nullable String strokeWidth, @Nullable String strokeColor, @Nullable String strokeDashArray, @Nullable String strokeLineCap, @Nullable String strokeLineJoin, @Nullable String strokeOpacity, @Nullable String offset) {
        super(config);
        this.strokeWidth = strokeWidth == null ? 0 : Float.parseFloat(strokeWidth);
        this.strokeColor = parseColorString(strokeColor, strokeOpacity == null ? 1 : Float.parseFloat(strokeOpacity));
        this.strokeDashArray = parseStrokeDashArray(strokeDashArray);
        this.strokeLineCap = parseStrokeLineCap(strokeLineCap);
        this.strokeLineJoin = parseStrokeLineJoin(strokeLineJoin);
        this.offset = offset == null ? 0 : Float.parseFloat(offset);
    }

    private static List<Float> parseStrokeDashArray(String dashArray) {
        if (dashArray == null) return null;
        List<Float> dashArrayList = new ArrayList<>();
        String[] dashArraySplit = dashArray.split(",");
        if (dashArraySplit.length == 0) return null;
        if (dashArraySplit.length % 2 != 0) {
            // Duplicate the list
            String[] dashArraySplitNew = new String[dashArraySplit.length * 2];
            System.arraycopy(dashArraySplit, 0, dashArraySplitNew, 0, dashArraySplit.length);
            System.arraycopy(dashArraySplit, 0, dashArraySplitNew, dashArraySplit.length, dashArraySplit.length);
            dashArraySplit = dashArraySplitNew;
        }

        for (int i = 0; i < dashArraySplit.length; i += 2) {
            float length = Float.parseFloat(dashArraySplit[i]);
            float gap = Float.parseFloat(dashArraySplit[i + 1]);
            dashArrayList.add(length);
            dashArrayList.add(gap);
        }
        return dashArrayList;
    }

    private static StrokeGenerator.StrokeLineCap parseStrokeLineCap(String strokeLineCap) {
        if (strokeLineCap == null) return StrokeGenerator.StrokeLineCap.BUTT;
        switch (strokeLineCap) {
            case "round":
                return StrokeGenerator.StrokeLineCap.ROUND;
            case "square":
                return StrokeGenerator.StrokeLineCap.SQUARE;
            default:
                return StrokeGenerator.StrokeLineCap.BUTT;
        }
    }

    private static StrokeGenerator.StrokeLineJoin parseStrokeLineJoin(String strokeLineJoin) {
        if (strokeLineJoin == null) return StrokeGenerator.StrokeLineJoin.MITER;
        switch (strokeLineJoin) {
            case "round":
                return StrokeGenerator.StrokeLineJoin.ROUND;
            case "bevel":
                return StrokeGenerator.StrokeLineJoin.BEVEL;
            default:
                return StrokeGenerator.StrokeLineJoin.MITER;
        }
    }

    private List<Float> dashArrayToLength(List<Float> strokeDashArray) {
        List<Float> lengths = new ArrayList<>();
        for (int i = 0; i < strokeDashArray.size(); i += 2) {
            float lengthPerPixel = config.getLengthPerPixel();
            float length = strokeDashArray.get(i) * lengthPerPixel;
            float gap = strokeDashArray.get(i + 1) * lengthPerPixel;
            lengths.add(length);
            lengths.add(gap);
        }
        return lengths;
    }

    private LineStrip convertToOffsetLineStrip(LineStrip lineStrip) {
        return StrokeGenerator.offsetLineStrip(lineStrip, offset * config.getLengthPerPixel(), strokeLineJoin);
    }

    private List<LineStrip> convertToDashedLineStrips(LineStrip linestrip, List<Float> strokeDashArray) {
        List<LineStrip> dashedLineStrips = new ArrayList<>();
        boolean isDash = true;
        int dashIndex = 0;

        List<Point> currentSegment = new ArrayList<>();
        List<Float> convertedDashArray = dashArrayToLength(strokeDashArray);
        float remainingDashLength = convertedDashArray.get(dashIndex);

        List<Point> lineStripPoints = linestrip.points;
        for (int i = 0; i < lineStripPoints.size() - 1; i++) {
            Point p1 = lineStripPoints.get(i);
            Point p2 = lineStripPoints.get(i + 1);
            Vector v12 = new Vector(p1, p2);
            Point curP = p1;

            float segmentLength = curP.distance(p2);
            float remainingSegmentLength = segmentLength;

            while (remainingSegmentLength > 0) {
                float splitLength = Math.min(remainingDashLength, remainingSegmentLength);

                float ratio = splitLength / segmentLength;
                Point splitPoint = curP.add(v12.mul(ratio));

                if (isDash) {
                    currentSegment.add(curP);
                    currentSegment.add(splitPoint);
                }

                curP = splitPoint;
                remainingSegmentLength -= splitLength;
                remainingDashLength -= splitLength;

                if (remainingDashLength <= 0) {
                    isDash = !isDash;
                    dashIndex = (dashIndex + 1) % convertedDashArray.size();
                    remainingDashLength = convertedDashArray.get(dashIndex);
                    if (currentSegment.size() > 0) {
                        dashedLineStrips.add(new LineStrip(currentSegment));
                    }
                    currentSegment = new ArrayList<>();
                }
            }
        }

        return dashedLineStrips;
    }

    private float[] drawLineStrip(LineStrip lineStrip) {
        float strokeWidth = this.strokeWidth * config.getLengthPerPixel();
        TriangleStrip triangleStrip = StrokeGenerator.generateStroke(lineStrip, 8, strokeWidth / 2, strokeLineCap).toTriangleStrip();
        return ColorShaderProgram.toVertexData(triangleStrip, strokeColor);
    }

    @SuppressLint("NewApi")
    @Override
    public SymMeta toDrawable(Way way, String layerName) {
        float scaledPixel = CoordinateTransform.getScalePixel(config.getScaleDenominator());
        LineStrip lineStrip = new LineStrip(way.toPointList(config.getOriginX(), config.getOriginY(), scaledPixel * config.getLengthPerPixel()));

        lineStrip = convertToOffsetLineStrip(lineStrip);

        SymMeta rv = new LineSymMeta();

        if (strokeDashArray != null) {
            List<LineStrip> dashedLineStrips = convertToDashedLineStrips(lineStrip, strokeDashArray);
            for (LineStrip dashedLineStrip : dashedLineStrips) {
                rv = rv.append(new LineSymMeta(drawLineStrip(dashedLineStrip)));
            }
            return rv;
        } else {
            rv = new LineSymMeta(drawLineStrip(lineStrip));
        }

        return rv;
    }

    @NonNull
    @Override
    public String toString() {
        return "<LineSymbolizer stroke-width=\"" + strokeWidth + "\" stroke=\"" + Arrays.toString(strokeColor) + "\" stroke-dasharray=\"" + strokeDashArray + "\" stroke-linecap=\"" + strokeLineCap + "\" stroke-linejoin=\"" + strokeLineJoin + "\" offset=\"" + offset + "\" />";
    }
}
