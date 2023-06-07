/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package com.hcmut.admin.utrafficsystem.tbt.programs;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUseProgram;

import com.hcmut.admin.utrafficsystem.tbt.utils.Config;
import com.hcmut.admin.utrafficsystem.tbt.utils.ShaderHelper;
import com.hcmut.admin.utrafficsystem.tbt.utils.TextResourceReader;

import java.util.ArrayList;
import java.util.List;

public abstract class ShaderProgram {
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

    protected final int program;
    protected final Config config;

    protected ShaderProgram(Config config, int vertexShaderResourceId,
                            int fragmentShaderResourceId) {
        this.config = config;
        // Compile the shaders and link the program.
        program = ShaderHelper.buildProgram(
                TextResourceReader
                        .readTextFileFromResource(config.context, vertexShaderResourceId),
                TextResourceReader
                        .readTextFileFromResource(config.context, fragmentShaderResourceId));
    }


    public int getUniformLocation(String uniformName) {
        return glGetUniformLocation(program, uniformName);
    }

    public int getAttributeLocation(String attributeName) {
        return glGetAttribLocation(program, attributeName);
    }

    protected static List<VertexAttrib> getVertexAttribs(String[] vertexAttribNames, int[] vertexAttribs) {
        List<VertexAttrib> result = new ArrayList<>();
        VertexAttrib prev = null;
        for (int i = 0; i < vertexAttribs.length; i++) {
            VertexAttrib attrib = new VertexAttrib(vertexAttribs[i], vertexAttribNames[i], prev);
            result.add(attrib);
            prev = attrib;
        }
        return result;
    }

    public abstract List<VertexAttrib> getVertexAttribs();
    public abstract int getTotalVertexAttribCount();

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program);
    }
}
