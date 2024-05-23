package com.example.sunrise.navigation;

import com.example.sunrise.R;
import com.example.sunrise.adapters.NavigationAdapter;

public enum ProfileNavigationRoutes implements NavigationItem {
    Feedback("Feedback", R.drawable.diamond_24px),
    Settings("Settings", R.drawable.settings_24px),
    About("About", R.drawable.info_24px);

    private final String destination;
    private final int iconId;

    ProfileNavigationRoutes(String destination, int iconId) {
        this.destination = destination;
        this.iconId = iconId;
    }

    public String getDestination() {
        return destination;
    }

    public int getIconId() {
        return iconId;
    }
}
