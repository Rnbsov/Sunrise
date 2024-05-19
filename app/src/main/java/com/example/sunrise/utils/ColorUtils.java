package com.example.sunrise.utils;

import android.graphics.Color;

public class ColorUtils {

    public static int darkenColor(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor; // Reduce brightness by the factor
        return Color.HSVToColor(hsv);
    }
}