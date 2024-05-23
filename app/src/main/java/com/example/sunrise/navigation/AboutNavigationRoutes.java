package com.example.sunrise.navigation;

import com.example.sunrise.R;

public enum AboutNavigationRoutes implements NavigationItem {
    GitHub("Source Code", R.drawable.github_mark_24px),
    Roadmap("Features Roadmap", R.drawable.rocket_launch_24px),
    Releases("Releases", R.drawable.new_releases_24px);

    private final String destination;
    private final int iconId;

    AboutNavigationRoutes(String destination, int iconId) {
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
