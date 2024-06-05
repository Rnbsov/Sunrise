package com.example.sunrise.constants;

import android.graphics.Color;

public enum ColorsEnum {
    PASTEL_RED("#FFCCCC"),
    PASTEL_ORANGE("#FFE5CC"),
    PASTEL_YELLOW("#FFF2CC"),
    PASTEL_GREEN("#CCFFCC"),
    PASTEL_BLUE("#CCE5FF"),
    PASTEL_PURPLE("#FFCCFF"),
    PASTEL_PINK("#FFCCEE"),
    PASTEL_BROWN("#D2B48C"),
    PASTEL_TEAL("#B2DFDB"),
    PASTEL_INDIGO("#C5CAE9"),
    PASTEL_LIME("#F0F4C3"),
    CRIMSON("#d2382e"),
    SALMON_PINK("#e54456"),
    RUBY_RED("#d12d60"),
    MAGENTA("#cc33a7"),
    AMETHYST("#bf32cc"),
    VIOLET("#8c4de6"),
    LAVENDER("#7667e4"),
    ROYAL_BLUE("#496ed9"),
    CERULEAN("#3486c2"),
    SEA_GREEN("#409ea6"),
    EMERALD("#35a17d"),
    MINT_GREEN("#45a160"),
    OLIVE("#449932"),
    GRASS_GREEN("#73ac37"),
    PEA_GREEN("#8fac38"),
    GOLDENROD("#ac961f"),
    ORANGE_PEEL("#ec9512"),
    PUMPKIN("#ec8013"),
    BURNT_ORANGE("#cc6733"),
    FIREBRICK("#cd4d34"),
    TRANSPARENT("#00FFFFFF");

    private final int color;

    ColorsEnum(String hex) {
        this.color = Color.parseColor(hex);
    }

    public int getColor() {
        return color;
    }
}
