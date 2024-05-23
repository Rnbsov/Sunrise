package com.example.sunrise;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sunrise.helpers.TaskCreationHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFabButton();
        setupNavigation();
        setupOnBackPressed();

        // Enable disk persistence, so app can be used offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    private void setupNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragments_container);

        assert navHostFragment != null : "navHostFragment should not be null";

        NavController navController = navHostFragment.getNavController();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.page_my_day, R.id.page_statistics, R.id.page_categories, R.id.page_profile).build();

        // Set up Toolbar with NavController
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        // Set up BottomNavigationView with NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Set destination listener, to hide fab and bottom navigation bar when navigation anywhere except main pages
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();
            if (destinationId == R.id.page_my_day || destinationId == R.id.page_statistics || destinationId == R.id.page_categories || destinationId == R.id.page_profile) {
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
     * Makes it possible to ask user whether they really want to exit the app
     */
    private void setupOnBackPressed() {
        // Register the OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmationDialog();
            }
        });
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
}