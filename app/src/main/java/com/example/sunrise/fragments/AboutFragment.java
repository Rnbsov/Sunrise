package com.example.sunrise.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sunrise.BuildConfig;
import com.example.sunrise.R;

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
}