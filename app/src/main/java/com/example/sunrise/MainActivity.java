package com.example.sunrise;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sunrise.helpers.TaskCreationHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private NavController navController; // Store a reference to NavController

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTheme(); // Choose either dynamic or custom

        // Enable edge-to-edge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Adjust the insets for the main activity
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        setupFabButton(); // Initialize and set up the fab
        setupNavigation(); // Setup navigation logic
        setupOnBackPressed(); // Set up the behavior for when the back button is pressed
    }

    private void setupNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragments_container);

        assert navHostFragment != null : "navHostFragment should not be null";

        navController = navHostFragment.getNavController(); // Initialize NavController
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.page_my_day, R.id.page_statistics, R.id.page_workspaces, R.id.page_categories, R.id.page_profile).build();

        // Set up Toolbar with NavController
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        // Set up BottomNavigationView with NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Set destination listener, to hide fab and bottom navigation bar when navigating anywhere except main pages
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (isAtRootDestination()) {
                showBottomNavBarAndFab();
            } else {
                hideBottomNavBarAndFab();
            }
        });
    }

    private void hideBottomNavBarAndFab() {
        bottomNavigationView.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
    }

    private void showBottomNavBarAndFab() {
        bottomNavigationView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
    }

    private void setupFabButton() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            TaskCreationHelper taskCreationHelper = new TaskCreationHelper(MainActivity.this);
            taskCreationHelper.showTaskCreationDialog(v);
        });
    }

    /**
     * Sets up the back pressed behavior to show an exit confirmation dialog at the root level.
     */
    private void setupOnBackPressed() {
        // Register the OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Check if the current destination is one of the root destinations
                if (isAtRootDestination()) {
                    showExitConfirmationDialog();
                } else {
                    // Otherwise, let the NavController handle the back press (navigate up the stack)
                    navController.navigateUp();
                }
            }
        });
    }

    /**
     * Checks if the current destination is one of the root destinations.
     *
     * @return True if the current destination is a root destination, false otherwise.
     */
    private boolean isAtRootDestination() {
        int currentDestinationId = navController.getCurrentDestination().getId();
        return currentDestinationId == R.id.page_my_day ||
                currentDestinationId == R.id.page_statistics ||
                currentDestinationId == R.id.page_workspaces ||
                currentDestinationId == R.id.page_categories ||
                currentDestinationId == R.id.page_profile;
    }

    /**
     * Shows a confirmation dialog asking the user if they want to exit the app.
     * If the user selects "Yes", the app will be closed.
     */
    private void showExitConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.dialog_exit_title)
                .setMessage(R.string.dialog_exit_message)
                .setPositiveButton(R.string.dialog_exit_positive, (dialog, which) -> {
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                })
                .setNegativeButton(R.string.dialog_exit_negative, null)
                .show();
    }

    private void setupTheme() {
        if (true) {
            // Set status bar color for dynamic theme
            setTheme(R.style.Theme_Sunrise_Dynamic);
            setStatusBarColor(com.google.android.material.R.attr.colorOnPrimary);
        } else {
            // Set status bar color
            setTheme(R.style.Theme_Sunrise);
            setStatusBarColor(R.attr.appBarColor);
        }
    }

    private void setStatusBarColor(int resId) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(resId, typedValue, true);
        int color = typedValue.data;
        getWindow().setStatusBarColor(color);
    }
}