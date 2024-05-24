package com.example.sunrise.constants;

import com.example.sunrise.R;

public enum Icon {
    LABEL(R.drawable.label_24px),
    PALETTE(R.drawable.palette_24px),
    FLOWER(R.drawable.flower_24px),
    DIAMOND(R.drawable.diamond_24px);

    private final int resId;

    Icon(int resId) {
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }
}
