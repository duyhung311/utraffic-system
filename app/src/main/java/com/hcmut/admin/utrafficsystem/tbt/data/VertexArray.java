/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package com.hcmut.admin.utrafficsystem.tbt.data;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

import android.opengl.GLES20;
import android.util.Log;

import com.hcmut.admin.utrafficsystem.tbt.programs.ShaderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

public class VertexArray {
    private static final int BYTES_PER_FLOAT = 4;
    private final ShaderProgram shaderProgram;
    private final int vertexCount;
    private int id = -1;

    private void genBuffer(float[] vertexData, int drawType) {
        if (id != -1) {
            GLES20.glDeleteBuffers(1, new int[]{id}, 0);
        }
        FloatBuffer floatBuffer = ByteBuffer
                .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        floatBuffer.position(0);
        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, floatBuffer.capacity() * BYTES_PER_FLOAT, floatBuffer, drawType);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        id = vbo[0];
//        Log.d("VertexArray", "genBuffer: " + id);
    }

    public VertexArray(ShaderProgram shaderProgram, float[] vertexData, int drawType) {
        this.shaderProgram = shaderProgram;
        this.vertexCount = vertexData.length / shaderProgram.getTotalVertexAttribCount();
        genBuffer(vertexData, drawType);
    }

    public VertexArray(ShaderProgram shaderProgram, float[] vertexData) {
        this(shaderProgram, vertexData, GLES20.GL_STATIC_DRAW);
    }

    public void changeData(float[] vertexData) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);
        FloatBuffer floatBuffer = ByteBuffer
                .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        floatBuffer.position(0);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, floatBuffer.capacity() * BYTES_PER_FLOAT, floatBuffer);
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("VertexArray", "changeData: " + error);
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation,
                                       int componentCount, int strideInElements) {
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT,
                false, strideInElements * BYTES_PER_FLOAT, dataOffset * BYTES_PER_FLOAT);
        glEnableVertexAttribArray(attributeLocation);
    }

    public void setDataFromVertexData() {
        shaderProgram.useProgram();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);
        List<ShaderProgram.VertexAttrib> attribs = shaderProgram.getVertexAttribs();
        int totalComponents = shaderProgram.getTotalVertexAttribCount();
        for (ShaderProgram.VertexAttrib attrib : attribs) {
            int location = shaderProgram.getAttributeLocation(attrib.name);
            if (location == -1) {
//                Log.e("VertexArray", "setDataFromVertexData: " + attrib.name + " not found");
                continue;
            }
            setVertexAttribPointer(attrib.offset, location, attrib.count, totalComponents);
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    @Override
    protected void finalize() throws Throwable {
//        Log.d("VertexArray", "finalize: " + id);
        if (id != -1) {
            GLES20.glDeleteBuffers(1, new int[]{id}, 0);
        }
        super.finalize();
    }
}
