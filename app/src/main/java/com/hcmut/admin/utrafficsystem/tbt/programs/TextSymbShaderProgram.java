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
public class TextSymbShaderProgram extends ShaderProgram {
    public static final String U_PROJECTION_MATRIX = "u_ProjectionMatrix";
    public static final String U_MODEL_VIEW_MATRIX = "u_ModelViewMatrix";
    public static final String U_ROTATION_ANGLE = "u_RotationAngle";
    public static final String U_SCALE = "u_Scale";
    public static final String U_TRANSLATION = "u_Translation";
    public static final String U_TEXT_CENTER = "u_TextCenter";
    public static final String A_POSITION = "a_Position";
    public static final String A_FONT_SIZE = "a_FontSize";
    public static final String A_ANGLE = "a_Angle";
    public static final String A_OFFSET = "a_Offset";
    public static final String A_ALT_ANGLE = "a_AltAngle";
    public static final String A_ALT_OFFSET = "a_AltOffset";
    public static final String A_FIRST_ANGLE = "a_FirstAngle";
    public static final String A_MODE = "a_Mode";
    public static final String A_COLOR = "a_Color";
    private static final int[] VERTEX_ATTRIBS = new int[]{
            2, 1, 1, 2, 1, 2, 1, 1, 4
    };
    private static final String[] VERTEX_ATTRIB_NAMES = new String[]{
            A_POSITION, A_FONT_SIZE, A_ANGLE, A_OFFSET, A_ALT_ANGLE, A_ALT_OFFSET, A_FIRST_ANGLE, A_MODE, A_COLOR
    };
    public static final int TOTAL_VERTEX_ATTRIB_COUNT = Arrays.stream(VERTEX_ATTRIBS).reduce(0, Integer::sum);
    private static final List<VertexAttrib> VERTEX_ATTRIB_LIST = getVertexAttribs(VERTEX_ATTRIB_NAMES, VERTEX_ATTRIBS);
    private final float[] projectionMatrix;
    private final float[] modelViewMatrix;


    public TextSymbShaderProgram(Config config, float[] projectionMatrix, float[] modelViewMatrix) {
        super(config, R.raw.text_symb_vert,
                R.raw.text_symb_frag);
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

    public static float[] toLineVertexData(Point p, float fontSize, float angle, Point offset, float altAngle, Point altOffset, float firstAngle, float r, float g, float b, float a) {
        return new float[]{p.x, p.y, fontSize, angle, offset.x, offset.y, altAngle, altOffset.x, altOffset.y, firstAngle, 1, r, g, b, a};
    }

    public static float[] toLineVertexData(List<Point> points, float fontSize, float angle, Point offset, float altAngle, Point altOffset, float firstAngle, float r, float g, float b, float a) {
        if (points == null) {
            return new float[0];
        }
        float[] result = new float[points.size() * TOTAL_VERTEX_ATTRIB_COUNT];
        for (int i = 0; i < points.size(); i++) {
            float[] vertexData = toLineVertexData(points.get(i), fontSize, angle, offset, altAngle, altOffset, firstAngle, r, g, b, a);
            System.arraycopy(vertexData, 0, result, i * vertexData.length, vertexData.length);
        }
        return result;
    }

    public static float[] toLineVertexData(PointList p, float fontSize, float angle, Point offset, float altAngle, Point altOffset, float firstAngle, float[] color) {
        if (p == null) {
            return new float[0];
        }
        return toLineVertexData(p.points, fontSize, angle, offset, altAngle, altOffset, firstAngle, color[0], color[1], color[2], color[3]);
    }

    public static float[] toLineVertexData(List<Point> points, float fontSize, float angle, Point offset, float altAngle, Point altOffset, float firstAngle, float[] color) {
        return toLineVertexData(points, fontSize, angle, offset, altAngle, altOffset, firstAngle, color[0], color[1], color[2], color[3]);
    }

    public static float[] toPointVertexData(Point p, float fontSize, Point offset, Point center, float r, float g, float b, float a) {
        return new float[]{p.x, p.y, fontSize, 0, offset.x, offset.y, 0, center.x, center.y, 0, 0, r, g, b, a};
    }

    public static float[] toPointVertexData(List<Point> points, float fontSize, Point offset, Point center, float r, float g, float b, float a) {
        if (points == null) {
            return new float[0];
        }
        float[] result = new float[points.size() * TOTAL_VERTEX_ATTRIB_COUNT];
        for (int i = 0; i < points.size(); i++) {
            float[] vertexData = toPointVertexData(points.get(i), fontSize, offset, center, r, g, b, a);
            System.arraycopy(vertexData, 0, result, i * vertexData.length, vertexData.length);
        }
        return result;
    }

    public static float[] toPointVertexData(PointList p, float fontSize, Point offset, Point center, float[] color) {
        if (p == null) {
            return new float[0];
        }
        return toPointVertexData(p.points, fontSize, offset, center, color[0], color[1], color[2], color[3]);
    }

    public static float[] toPointVertexData(List<Point> points, float fontSize, Point offset, Point center, float[] color) {
        return toPointVertexData(points, fontSize, offset, center, color[0], color[1], color[2], color[3]);
    }
}
