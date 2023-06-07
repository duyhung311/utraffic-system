package com.hcmut.admin.utrafficsystem.tbt.utils;

import android.opengl.GLSurfaceView;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class MultisampleConfigChooser implements GLSurfaceView.EGLConfigChooser {
    private static final String TAG = MultisampleConfigChooser.class.getSimpleName();
    private static final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
    private static final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;
    private static final int RED = 8;
    private static final int GREEN = 8;
    private static final int BLUE = 8;
    private static final int ALPHA = 8;
    private static final int STENCIL = 8;
    private static final int DEPTH = 16;

    private static int findConfigAttrib(@NonNull EGL10 gl, @NonNull EGLDisplay display, @NonNull EGLConfig config, int attribute, int defaultValue, int[] tmp) {
        if (gl.eglGetConfigAttrib(display, config, attribute, tmp)) {
            return tmp[0];
        }
        return defaultValue;
    }

    @Override
    public EGLConfig chooseConfig(@NonNull EGL10 gl, @NonNull EGLDisplay display) {
        // try to find a normal multisample configuration first.
        EGLConfig config = ConfigData.create(EGL10.EGL_RED_SIZE, RED,
                EGL10.EGL_GREEN_SIZE, GREEN,
                EGL10.EGL_BLUE_SIZE, BLUE,
                EGL10.EGL_ALPHA_SIZE, ALPHA,
                EGL10.EGL_STENCIL_SIZE, STENCIL,
                EGL10.EGL_DEPTH_SIZE, DEPTH,
                EGL10.EGL_RENDERABLE_TYPE, 4 /* EGL_OPENGL_ES2_BIT */,
                EGL10.EGL_SAMPLE_BUFFERS, 1 /* true */,
                EGL10.EGL_SAMPLES, 4,
                EGL10.EGL_NONE).tryConfig(gl, display);
        if (config != null) {
            Log.d(TAG, "Using normal multisampling");
            return config;
        }

        // no normal multisampling config was found. Try to create a
        // coverage multisampling configuration, for the nVidia Tegra2.
        // See the EGL_NV_coverage_sample documentation.
        config = ConfigData.create(EGL10.EGL_RED_SIZE, RED,
                EGL10.EGL_GREEN_SIZE, GREEN,
                EGL10.EGL_BLUE_SIZE, BLUE,
                EGL10.EGL_ALPHA_SIZE, ALPHA,
                EGL10.EGL_STENCIL_SIZE, STENCIL,
                EGL10.EGL_DEPTH_SIZE, DEPTH,
                EGL10.EGL_RENDERABLE_TYPE, 4 /* EGL_OPENGL_ES2_BIT */,
                EGL_COVERAGE_BUFFERS_NV, 1 /* true */,
                EGL_COVERAGE_SAMPLES_NV, 2,  // always 5 in practice on tegra 2
                EGL10.EGL_NONE).tryConfig(gl, display);
        if (config != null) {
            Log.d(TAG, "Using coverage multisampling");
            return config;
        }

        // fallback to simple configuration
        config = ConfigData.create(
                EGL10.EGL_RED_SIZE, RED,
                EGL10.EGL_GREEN_SIZE, GREEN,
                EGL10.EGL_BLUE_SIZE, BLUE,
                EGL10.EGL_ALPHA_SIZE, ALPHA,
                EGL10.EGL_STENCIL_SIZE, STENCIL,
                EGL10.EGL_DEPTH_SIZE, DEPTH,
                EGL10.EGL_NONE).tryConfig(gl, display);
        if (config != null) {
            Log.d(TAG, "Using no multisampling");
            return config;
        }

        throw new IllegalArgumentException("No supported configuration found");
    }

    private static final class ConfigData {
        final int[] spec;

        private ConfigData(int[] spec) {
            this.spec = spec;
        }

        @NonNull
        private static ConfigData create(int... spec) {
            return new ConfigData(spec);
        }

        @Nullable
        private EGLConfig tryConfig(@NonNull EGL10 gl, @NonNull EGLDisplay display) {
            final int[] tmp = new int[1];

            if (!gl.eglChooseConfig(display, spec, null, 0, tmp)) {
                return null;
            }
            final int count = tmp[0];
            if (count > 0) {
                // get all matching configurations
                final EGLConfig[] configs = new EGLConfig[count];
                if (!gl.eglChooseConfig(display, spec, configs, count, tmp)) {
                    return null;
                }

                return configs[0];
            }

            return null;
        }
    }
}
