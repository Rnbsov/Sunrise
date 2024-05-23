package com.example.sunrise.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sunrise.BuildConfig;
import com.example.sunrise.R;
import com.example.sunrise.adapters.NavigationAdapter;
import com.example.sunrise.navigation.AboutNavigationRoutes;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up version information
        setupVersionInfo(view);

        // Setup navigation routes
        setupNavigationRoutes(view);
    }

    /**
     * Sets up the version name and version code TextViews with the corresponding values from BuildConfig.
     * @param view The root view of the fragment layout.
     */
    private void setupVersionInfo(View view) {
        // Find the TextViews
        TextView versionTextView = view.findViewById(R.id.version);

        // Retrieve version name and version code from BuildConfig
        String versionName = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;

        // Format the version string
        String versionText = String.format(Locale.US, "%s (%d)", versionName, versionCode);

        // Set the version name and version code to the TextView
        versionTextView.setText(versionText);
    }

    /**
     * Sets up the navigation routes in the RecyclerView.
     */
    private void setupNavigationRoutes(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.navigation_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true); // Enable some optimization of recyclerView

        List<AboutNavigationRoutes> navigationItems = Arrays.asList(AboutNavigationRoutes.values());
        NavigationAdapter<AboutNavigationRoutes> adapter = new NavigationAdapter<>(navigationItems, item -> {
            switch (item) {
                case GitHub -> handleGithubClick();
                case Roadmap -> handleRoadmapClick();
                case Releases -> handleReleasesClick();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    /**
     * Handles the click event for the GitHub route
     * Opens the GitHub repo of the project in a browser.
     */
    private void handleGithubClick() {
        openUrl("https://github.com/Rnbsov/Sunrise");
    }

    /**
     * Handles the click event for the Roadmap route
     */
    private void handleRoadmapClick() {
        openUrl("https://github.com/users/Rnbsov/projects/2/");
    }

    /**
     * Handles the click event for the Releases route
     */
    private void handleReleasesClick() {
        openUrl("https://github.com/Rnbsov/Sunrise/releases");
    }

    /**
     * Opens the given URL in a browser.
     * @param url The URL to be opened.
     */
    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}