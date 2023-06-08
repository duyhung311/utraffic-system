package com.hcmut.admin.utraffictest.tbt.mapnik.symbolizer;

import android.opengl.GLES20;

import com.hcmut.admin.utraffictest.tbt.data.VertexArray;
import com.hcmut.admin.utraffictest.tbt.geometry.Point;
import com.hcmut.admin.utraffictest.tbt.programs.ColorShaderProgram;
import com.hcmut.admin.utraffictest.tbt.programs.TextSymbShaderProgram;
import com.hcmut.admin.utraffictest.tbt.utils.Config;

import java.util.ArrayList;
import java.util.List;

public class CombinedSymMeta extends SymMeta {
    private final List<float[]> lineDrawables;
    private final List<float[]> polygonDrawables;
    private final List<float[]> textPolygonDrawables;
    private final List<float[]> textLineDrawables;
    private int firstHalfCount = 0;
    private int textFirstHalfCount = 0;
    private float[] shapeDrawable = null;
    private float[] textDrawable = null;
    private VertexArray vertexArray = null;
    private VertexArray textVertexArray = null;
    private ColorShaderProgram colorShaderProgram = null;
    private TextSymbShaderProgram textSymbShaderProgram = null;

    public CombinedSymMeta() {
        lineDrawables = new ArrayList<>(0);
        polygonDrawables = new ArrayList<>(0);
        textPolygonDrawables = new ArrayList<>(0);
        textLineDrawables = new ArrayList<>(0);
    }

    private CombinedSymMeta(List<float[]> lineDrawables, List<float[]> polygonDrawables, List<float[]> textPolygonDrawables, List<float[]> textLineDrawables) {
        this.lineDrawables = lineDrawables;
        this.polygonDrawables = polygonDrawables;
        this.textPolygonDrawables = textPolygonDrawables;
        this.textLineDrawables = textLineDrawables;
    }

    public CombinedSymMeta(List<SymMeta> symMetas) {
        lineDrawables = new ArrayList<>();
        polygonDrawables = new ArrayList<>();
        textPolygonDrawables = new ArrayList<>();
        textLineDrawables = new ArrayList<>();
        for (SymMeta symMeta : symMetas) {
            List<float[]>[] symMetaDrawables = extractSymMeta(symMeta);
            lineDrawables.addAll(symMetaDrawables[0]);
            polygonDrawables.addAll(symMetaDrawables[1]);
            textPolygonDrawables.addAll(symMetaDrawables[2]);
            textLineDrawables.addAll(symMetaDrawables[3]);
        }
    }

    private static List<float[]>[] extractSymMeta(SymMeta symMeta) {
        List<float[]> lineDrawables;
        List<float[]> polygonDrawables;
        List<float[]> textPolygonDrawables;
        List<float[]> textLineDrawables;
        if (symMeta instanceof CombinedSymMeta) {
            lineDrawables = ((CombinedSymMeta) symMeta).lineDrawables;
            polygonDrawables = ((CombinedSymMeta) symMeta).polygonDrawables;
            textPolygonDrawables = ((CombinedSymMeta) symMeta).textPolygonDrawables;
            textLineDrawables = ((CombinedSymMeta) symMeta).textLineDrawables;
        } else if (symMeta instanceof LineSymbolizer.LineSymMeta) {
            lineDrawables = ((LineSymbolizer.LineSymMeta) symMeta).drawables;
            polygonDrawables = new ArrayList<>(0);
            textPolygonDrawables = new ArrayList<>(0);
            textLineDrawables = new ArrayList<>(0);
        } else if (symMeta instanceof PolygonSymbolizer.PolygonSymMeta) {
            lineDrawables = new ArrayList<>(0);
            polygonDrawables = ((PolygonSymbolizer.PolygonSymMeta) symMeta).drawables;
            textPolygonDrawables = new ArrayList<>(0);
            textLineDrawables = new ArrayList<>(0);
        } else if (symMeta instanceof TextSymbolizer.TextSymMeta) {
            lineDrawables = new ArrayList<>(0);
            polygonDrawables = new ArrayList<>(0);
            textPolygonDrawables = ((TextSymbolizer.TextSymMeta) symMeta).polygonDrawables;
            textLineDrawables = ((TextSymbolizer.TextSymMeta) symMeta).lineDrawables;
        } else {
            lineDrawables = new ArrayList<>(0);
            polygonDrawables = new ArrayList<>(0);
            textPolygonDrawables = new ArrayList<>(0);
            textLineDrawables = new ArrayList<>(0);
        }

        return new List[]{lineDrawables, polygonDrawables, textPolygonDrawables, textLineDrawables};
    }

    @Override
    public boolean isEmpty() {
        return vertexArray == null && lineDrawables.isEmpty() && polygonDrawables.isEmpty() && textPolygonDrawables.isEmpty() && textLineDrawables.isEmpty();
    }

    @Override
    public SymMeta append(SymMeta other) {
        if (other == null || other.isEmpty()) return this;

        List<float[]>[] otherDrawables = extractSymMeta(other);
        List<float[]> newLineDrawables = new ArrayList<>(lineDrawables);
        List<float[]> newPolygonDrawables = new ArrayList<>(polygonDrawables);
        List<float[]> newTextPolygonDrawables = new ArrayList<>(textPolygonDrawables);
        List<float[]> newTextLineDrawables = new ArrayList<>(textLineDrawables);
        newLineDrawables.addAll(otherDrawables[0]);
        newPolygonDrawables.addAll(otherDrawables[1]);
        newTextPolygonDrawables.addAll(otherDrawables[2]);
        newTextLineDrawables.addAll(otherDrawables[3]);
        return new CombinedSymMeta(newLineDrawables, newPolygonDrawables, newTextPolygonDrawables, newTextLineDrawables);
    }

//    @Override
//    public void draw(Config config) {
//        for (SymMeta symMeta : symMetas) {
//            symMeta.draw(config);
//        }
//    }


    private float[] getDrawable() {
        float[] triDrawable = appendRegular(polygonDrawables);
        float[] triStripDrawable = appendTriangleStrip(lineDrawables, ColorShaderProgram.TOTAL_VERTEX_ATTRIB_COUNT);
        float[] drawable = appendRegular(triStripDrawable, triDrawable);
        firstHalfCount = triStripDrawable.length / ColorShaderProgram.TOTAL_VERTEX_ATTRIB_COUNT;
        return drawable;
    }

    private float[] getTextDrawable() {
        float[] triDrawable = appendRegular(textPolygonDrawables);
        float[] triStripDrawable = appendTriangleStrip(textLineDrawables, TextSymbShaderProgram.TOTAL_VERTEX_ATTRIB_COUNT);
        float[] drawable = appendRegular(triStripDrawable, triDrawable);
        textFirstHalfCount = triStripDrawable.length / TextSymbShaderProgram.TOTAL_VERTEX_ATTRIB_COUNT;
        return drawable;
    }


    private void draw(ColorShaderProgram shaderProgram) {
        if (vertexArray == null && shapeDrawable == null && lineDrawables.isEmpty() && polygonDrawables.isEmpty()) return;
        if (vertexArray == null) {
            assert shapeDrawable != null;
            vertexArray = new VertexArray(shaderProgram, shapeDrawable);
            shapeDrawable = null;
        }
        shaderProgram.useProgram();
        vertexArray.setDataFromVertexData();
        int pointCount = vertexArray.getVertexCount();
        if (firstHalfCount > 0) {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, firstHalfCount);
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, firstHalfCount, pointCount - firstHalfCount);
    }

    private void drawText(TextSymbShaderProgram shaderProgram) {
        if (textVertexArray == null && textDrawable == null && textLineDrawables.isEmpty() && textPolygonDrawables.isEmpty()) return;

        if (textVertexArray == null) {
            assert textDrawable != null;
            textVertexArray = new VertexArray(shaderProgram, textDrawable);
            textDrawable = null;
        }

        textVertexArray.setDataFromVertexData();
        Point textCenter = TextSymbolizer.TextDrawable.getTextCenter();
        GLES20.glUniform2f(shaderProgram.getUniformLocation(TextSymbShaderProgram.U_TEXT_CENTER), textCenter.x, textCenter.y);
        int pointCount = textVertexArray.getVertexCount();
        if (textFirstHalfCount > 0) {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, textFirstHalfCount);
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, textFirstHalfCount, pointCount - textFirstHalfCount);
    }

    @Override
    public void save(Config config) {
        colorShaderProgram = config.getColorShaderProgram();
        textSymbShaderProgram = config.getTextSymbShaderProgram();
        if (shapeDrawable == null && !(lineDrawables.isEmpty() && polygonDrawables.isEmpty())) {
            shapeDrawable = getDrawable();
            lineDrawables.clear();
            polygonDrawables.clear();
        }
        if (textDrawable == null && !(textLineDrawables.isEmpty() && textPolygonDrawables.isEmpty())) {
            textDrawable = getTextDrawable();
            textLineDrawables.clear();
            textPolygonDrawables.clear();
        }
    }

    @Override
    public void draw() {
        draw(colorShaderProgram);
        drawText(textSymbShaderProgram);
    }
}
