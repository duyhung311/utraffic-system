package com.hcmut.admin.utrafficsystem.model.osm.data;

import com.hcmut.admin.utrafficsystem.model.osm.geometry.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VertexData {
    public static class VertexAttrib {
        public final int count;
        public final String name;
        public final int offset;

        public VertexAttrib(int count, String name, VertexAttrib prev) {
            this.count = count;
            this.name = name;
            this.offset = prev == null ? 0 : prev.offset + prev.count;
        }
    }

    private static final int[] VERTEX_ATTRIBS = new int[]{
            3, 4,
    };

    private static final String[] VERTEX_ATTRIB_NAMES = new String[]{
            "a_Position",
            "a_Color",
    };

    private static final int seed = 69;
    private static final Random generator = new Random(seed);

    public static void resetRandom() {
        generator.setSeed(seed);
    }

    public static List<VertexAttrib> getVertexAttribs() {
        List<VertexAttrib> result = new ArrayList<>();
        VertexAttrib prev = null;
        for (int i = 0; i < VERTEX_ATTRIBS.length; i++) {
            VertexAttrib attrib = new VertexAttrib(VERTEX_ATTRIBS[i], VERTEX_ATTRIB_NAMES[i], prev);
            result.add(attrib);
            prev = attrib;
        }
        return result;
    }

    public static int getTotalComponentCount() {
        int result = 0;
        for (int vertexAttrib : VERTEX_ATTRIBS) {
            result += vertexAttrib;
        }
        return result;
    }

    public static float[] toVertexData(Point p, float r, float g, float b, float a) {
        return new float[]{p.x, p.y, p.z, r, g, b, a};
    }

    public static float[] toVertexData(Point p, boolean randomColor) {
        float r = randomColor ? generator.nextFloat() : 0;
        float g = randomColor ? generator.nextFloat() : 0;
        float b = randomColor ? generator.nextFloat() : 0;
        float a = 1;
        return toVertexData(p, r, g, b, a);
    }

    public static float[] toVertexData(Point p) {
        return toVertexData(p, false);
    }

    public static float[] toVertexData(List<Point> points, float r, float g, float b, float a) {
        float[] result = new float[points.size() * getTotalComponentCount()];
        for (int i = 0; i < points.size(); i++) {
            float[] vertexData = toVertexData(points.get(i), r, g, b, a);
            System.arraycopy(vertexData, 0, result, i * vertexData.length, vertexData.length);
        }
        return result;
    }

    public static float[] toVertexData(List<Point> points, boolean randomColor, boolean isUniform) {
        float r = randomColor ? generator.nextFloat() : 0;
        float g = randomColor ? generator.nextFloat() : 0;
        float b = randomColor ? generator.nextFloat() : 0;
        float a = 1;
        float[] result = new float[points.size() * getTotalComponentCount()];
        for (int i = 0; i < points.size(); i++) {
            float[] vertexData = isUniform && randomColor ? toVertexData(points.get(i), r, g, b, a) : toVertexData(points.get(i), randomColor);
            System.arraycopy(vertexData, 0, result, i * vertexData.length, vertexData.length);
        }
        return result;
    }

    public static float[] toVertexData(List<Point> points, boolean randomColor) {
        return toVertexData(points, randomColor, true);
    }

    public static float[] toVertexData(List<Point> points) {
        return toVertexData(points, false);
    }

    public static float[] toVertexData(Point[] points, float r, float g, float b, float a) {
        return toVertexData(List.of(points), r, g, b, a);
    }

    public static float[] toVertexData(Point[] points, boolean randomColor, boolean isUniform) {
        return toVertexData(List.of(points), randomColor, isUniform);
    }

    public static float[] toVertexData(Point[] points, boolean randomColor) {
        return toVertexData(List.of(points), randomColor);
    }

    public static float[] toVertexData(Point[] points) {
        return toVertexData(List.of(points), false);
    }
}
