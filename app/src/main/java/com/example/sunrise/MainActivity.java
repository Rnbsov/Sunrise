package com.example.sunrise;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.sunrise.helpers.TaskCreationHelper;
import com.example.sunrise.workers.ClearMyDayWorker;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private NavController navController; // Store a reference to NavController
    private static final String TAG_MY_DAY_CLEARING = "MyDayClearing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFabButton();
        setupNavigation();
        setupOnBackPressed();
        scheduleMyDayClearing(); // Schedules clearing MyDay everyday
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

    /**
     * Schedule the clearing of tasks in MyDay.
     *  This method sets up a periodic task using WorkManager to run every day at 9 am.
     *  If the device is not connected to a network, the task will not be executed.
     *  The initial delay is calculated to ensure that the task starts running from the next day at 9 am,
     *  even if the method is called later in the current day.
     */
    private void scheduleMyDayClearing() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Set the worker to run every day at 9 am
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long initialDelay = calendar.getTimeInMillis() - System.currentTimeMillis();
        if (initialDelay < 0) {
            // If the calculated delay is negative, it means the current time is past 9 am
            // Add 24 hours to delay to schedule for 9 am the next day
            initialDelay += 24 * 60 * 60 * 1000;
        }

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                ClearMyDayWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build();

        // Use enqueueUniquePeriodicWork to ensure only one work request is scheduled
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(TAG_MY_DAY_CLEARING, ExistingPeriodicWorkPolicy.KEEP, workRequest);
    }
}
