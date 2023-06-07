/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package com.hcmut.admin.utrafficsystem.tbt.programs;

import android.annotation.SuppressLint;
import android.opengl.GLES20;

import com.hcmut.admin.utrafficsystem.R;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Point;
import com.hcmut.admin.utrafficsystem.tbt.geometry.PointList;
import com.hcmut.admin.utrafficsystem.tbt.geometry.Vector;
import com.hcmut.admin.utrafficsystem.tbt.utils.Config;

import java.util.Arrays;
import java.util.List;

@SuppressLint("NewApi")
public class ColorShaderProgram extends ShaderProgram {
    public static final String U_PROJECTION_MATRIX = "u_ProjectionMatrix";
    public static final String U_MODEL_VIEW_MATRIX = "u_ModelViewMatrix";
    public static final String U_ROTATION_ANGLE = "u_RotationAngle";
    public static final String U_SCALE = "u_Scale";
    public static final String U_TRANSLATION = "u_Translation";
    public static final String A_POSITION = "a_Position";
    public static final String A_COLOR = "a_Color";
    private static final int[] VERTEX_ATTRIBS = new int[]{
            2, 4
    };
    private static final String[] VERTEX_ATTRIB_NAMES = new String[]{
            A_POSITION, A_COLOR
    };
    public static final int TOTAL_VERTEX_ATTRIB_COUNT = Arrays.stream(VERTEX_ATTRIBS).reduce(0, Integer::sum);
    private static final List<VertexAttrib> VERTEX_ATTRIB_LIST = getVertexAttribs(VERTEX_ATTRIB_NAMES, VERTEX_ATTRIBS);
    private final float[] projectionMatrix;
    private final float[] modelViewMatrix;


    public ColorShaderProgram(Config config, float[] projectionMatrix, float[] modelViewMatrix) {
        super(config, R.raw.color_vert,
                R.raw.color_frag);
        this.projectionMatrix = projectionMatrix;
        this.modelViewMatrix = modelViewMatrix;
    }

    @Override
    public void useProgram() {
        super.useProgram();

        int uMVPLocation = getUniformLocation(U_PROJECTION_MATRIX);
        GLES20.glUniformMatrix4fv(uMVPLocation, 1, false, projectionMatrix, 0);

        int uModelViewMatrix = getUniformLocation(U_MODEL_VIEW_MATRIX);
        GLES20.glUniformMatrix4fv(uModelViewMatrix, 1, false, modelViewMatrix, 0);

        int uRotationAngle = getUniformLocation(U_ROTATION_ANGLE);
        GLES20.glUniform1f(uRotationAngle, config.getRotation());

        int uScale = getUniformLocation(U_SCALE);
        GLES20.glUniform1f(uScale, config.getScale());

        int uTranslation = getUniformLocation(U_TRANSLATION);
        Vector translation = config.getTranslation();
        GLES20.glUniform2f(uTranslation, translation.x, translation.y);
    }

    @Override
    public List<VertexAttrib> getVertexAttribs() {
        return VERTEX_ATTRIB_LIST;
    }

    @Override
    public int getTotalVertexAttribCount() {
        return TOTAL_VERTEX_ATTRIB_COUNT;
    }

    public static float[] toVertexData(Point p, float r, float g, float b, float a) {
        return new float[]{p.x, p.y, r, g, b, a};
    }

    public static float[] toVertexData(List<Point> points, float r, float g, float b, float a) {
        if (points == null) {
            return new float[0];
        }
        float[] result = new float[points.size() * TOTAL_VERTEX_ATTRIB_COUNT];
        for (int i = 0; i < points.size(); i++) {
            float[] vertexData = toVertexData(points.get(i), r, g, b, a);
            System.arraycopy(vertexData, 0, result, i * vertexData.length, vertexData.length);
        }
        return result;
    }

    public static float[] toVertexData(PointList p, float r, float g, float b, float a) {
        if (p == null) {
            return new float[0];
        }
        return toVertexData(p.points, r, g, b, a);
    }

    public static float[] toVertexData(PointList p, float[] color) {
        return toVertexData(p, color[0], color[1], color[2], color[3]);
    }

    public static float[] toVertexData(List<Point> points, float[] color) {
        return toVertexData(points, color[0], color[1], color[2], color[3]);
    }
}
