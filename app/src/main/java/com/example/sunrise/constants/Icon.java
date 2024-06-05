package com.example.sunrise.constants;

import com.example.sunrise.R;

public enum Icon {
    PALETTE(R.drawable.palette_24px),
    FLOWER(R.drawable.flower_24px),
    DIAMOND(R.drawable.diamond_24px),
    AZM(R.drawable.azm_24px),
    BADGE(R.drawable.badge_24px),
    BUSINESS_CENTER(R.drawable.business_center_24px),
    ENGINEERING(R.drawable.engineering_24px),
    SCHOOL(R.drawable.school_24px),
    CALENDAR_CLOCK(R.drawable.calendar_clock_24px),
    GROUP_WORK(R.drawable.group_work_24px),
    HOME_WORK(R.drawable.home_work_24px),
    PUBLIC(R.drawable.public_24px),
    RECEIPT_LONG(R.drawable.receipt_long_24px),
    NEW_RELEASES(R.drawable.new_releases_24px),
    LABEL(R.drawable.label_24px);

    private final int resId;

    Icon(int resId) {
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }
}
