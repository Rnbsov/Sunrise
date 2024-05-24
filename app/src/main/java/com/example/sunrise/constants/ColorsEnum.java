package com.example.sunrise.constants;

import android.graphics.Color;

public enum ColorsEnum {
    PASTEL_RED("#FFCCCC"),
    PASTEL_ORANGE("#FFE5CC"),
    PASTEL_YELLOW("#FFF2CC"),
    PASTEL_GREEN("#CCFFCC"),
    PASTEL_BLUE("#CCE5FF"),
    PASTEL_PURPLE("#FFCCFF"),
    TRANSPARENT("#00FFFFFF");

    private final int color;

    ColorsEnum(String hex) {
        this.color = Color.parseColor(hex);
    }

    public int getColor() {
        return color;
    }
}