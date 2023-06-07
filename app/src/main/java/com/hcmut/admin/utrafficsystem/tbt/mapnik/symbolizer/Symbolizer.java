package com.hcmut.admin.utrafficsystem.tbt.mapnik.symbolizer;

import android.annotation.SuppressLint;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import com.hcmut.admin.utrafficsystem.tbt.osm.Way;
import com.hcmut.admin.utrafficsystem.tbt.utils.Config;

public abstract class Symbolizer {
    protected Config config;

    abstract public SymMeta toDrawable(Way way, String layerName);

    @NonNull
    public abstract String toString();

    protected Symbolizer(Config config) {
        this.config = config;
    }

    protected static float[] parseColorString(String colorString, float optionalOpacity) {
        if (colorString == null) return new float[]{0, 0, 0, optionalOpacity};

        if (colorString.startsWith("rgba")) {
            return parseColorRGBA(colorString);
        } else if (colorString.startsWith("rgb")) {
            return parseColorRGB(colorString, optionalOpacity);
        } else if (colorString.startsWith("#") && colorString.length() == 7) {
            return parseColorHex(colorString, optionalOpacity);
        } else if (colorString.startsWith("#") && colorString.length() == 9) {
            return parseColorHex(colorString);
        } else {
            throw new IllegalArgumentException("Invalid color string: " + colorString);
        }
    }

    protected static float[] parseColorRGBA(String colorString) {
        // color string format: rgba(r, g, b, a)
        String[] colorStringArray = colorString.substring(5, colorString.length() - 1).split(",");
        float[] colorVector = new float[4];
        colorVector[0] = Integer.parseInt(colorStringArray[0].trim()) / 255f;
        colorVector[1] = Integer.parseInt(colorStringArray[1].trim()) / 255f;
        colorVector[2] = Integer.parseInt(colorStringArray[2].trim()) / 255f;
        colorVector[3] = Float.parseFloat(colorStringArray[3].trim());
        return colorVector;
    }

    protected static float[] parseColorRGB(String colorString, float opacity) {
        // color string format: rgb(r, g, b)
        String[] colorStringArray = colorString.substring(4, colorString.length() - 1).split(",");
        float[] colorVector = new float[4];
        colorVector[0] = Integer.parseInt(colorStringArray[0].trim()) / 255f;
        colorVector[1] = Integer.parseInt(colorStringArray[1].trim()) / 255f;
        colorVector[2] = Integer.parseInt(colorStringArray[2].trim()) / 255f;
        colorVector[3] = opacity;
        return colorVector;
    }

    protected static float[] parseColorHex(String colorString, float opacity) {
        float[] colorVector = new float[4];
        colorVector[0] = Integer.parseInt(colorString.substring(1, 3), 16) / 255f;
        colorVector[1] = Integer.parseInt(colorString.substring(3, 5), 16) / 255f;
        colorVector[2] = Integer.parseInt(colorString.substring(5, 7), 16) / 255f;
        colorVector[3] = opacity;
        return colorVector;
    }

    protected static float[] parseColorHex(String colorString) {
        float[] colorVector = new float[4];
        colorVector[0] = Integer.parseInt(colorString.substring(1, 3), 16) / 255f;
        colorVector[1] = Integer.parseInt(colorString.substring(3, 5), 16) / 255f;
        colorVector[2] = Integer.parseInt(colorString.substring(5, 7), 16) / 255f;
        colorVector[3] = Integer.parseInt(colorString.substring(7, 9), 16) / 255f;
        return colorVector;
    }


    private static float[] darkenColor(float[] color, @FloatRange(from = 0.0, to = 1.0) float factor) {
        float[] newColor = new float[4];
        newColor[0] = color[0] * factor;
        newColor[1] = color[1] * factor;
        newColor[2] = color[2] * factor;
        newColor[3] = color[3];
        return newColor;
    }

    @SuppressLint("DefaultLocale")
    public static String darkenColor(String colorString, float optionalOpacity, @FloatRange(from = 0.0, to = 1.0) float factor) {
        float[] color = parseColorString(colorString, optionalOpacity);
        float[] newColor = darkenColor(color, factor);
        return String.format("rgba(%d, %d, %d, %f)", (int) (newColor[0] * 255), (int) (newColor[1] * 255), (int) (newColor[2] * 255), newColor[3]);
    }
}
