package com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer;

import android.annotation.SuppressLint;

import com.hcmut.admin.utrafficsystem.tbt.utils.Config;

import java.util.List;

@SuppressLint("NewApi")
public abstract class SymMeta {
    public abstract boolean isEmpty();
    public abstract SymMeta append(SymMeta other);
    public abstract void save(Config config);
    public abstract void draw();

    public static float[] appendTriangleStrip(float[] oldData, float[] newData, int totalVertexAttribCount) {
        if (oldData == null || oldData.length == 0) return newData;
        if (newData == null || newData.length == 0) return oldData;

        float[] result = new float[oldData.length + newData.length + totalVertexAttribCount * 2];
        int offset = 0;
        System.arraycopy(oldData, 0, result, offset, oldData.length);
        offset += oldData.length;
        System.arraycopy(oldData, oldData.length - totalVertexAttribCount, result, offset, totalVertexAttribCount);
        offset += totalVertexAttribCount;
        System.arraycopy(newData, 0, result, offset, totalVertexAttribCount);
        offset += totalVertexAttribCount;
        System.arraycopy(newData, 0, result, offset, newData.length);
        return result;
    }

    public static float[] appendRegular(float[] oldDrawable, float[] newDrawable) {
        float[] result = new float[oldDrawable.length + newDrawable.length];
        System.arraycopy(oldDrawable, 0, result, 0, oldDrawable.length);
        System.arraycopy(newDrawable, 0, result, oldDrawable.length, newDrawable.length);
        return result;
    }

    public static float[] appendRegular(List<float[]> drawables) {
        int totalSize = drawables.stream().mapToInt(drawable -> drawable.length).sum();
        float[] result = new float[totalSize];
        int offset = 0;
        for (float[] drawable : drawables) {
            System.arraycopy(drawable, 0, result, offset, drawable.length);
            offset += drawable.length;
        }
        return result;
    }

    public static float[] appendTriangleStrip(List<float[]> drawables, int totalVertexAttribCount) {
        int totalSize = drawables.stream().mapToInt(drawable -> drawable.length).sum();
        if (totalSize == 0) return new float[0];
        float[] result = new float[totalSize + (drawables.size() - 1) * totalVertexAttribCount * 2];
        int offset = 0;
        for (int i = 0; i < drawables.size(); i++) {
            float[] drawable = drawables.get(i);
            if (i > 0 && drawable.length > 0) {
                System.arraycopy(result, offset - totalVertexAttribCount, result, offset, totalVertexAttribCount);
                offset += totalVertexAttribCount;
                System.arraycopy(drawable, 0, result, offset, totalVertexAttribCount);
                offset += totalVertexAttribCount;
            }
            System.arraycopy(drawable, 0, result, offset, drawable.length);
            offset += drawable.length;
        }
        return result;
    }
}
