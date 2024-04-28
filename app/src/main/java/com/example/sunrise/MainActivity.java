package com.example.sunrise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import com.example.sunrise.fragments.CategoriesFragment;
import com.example.sunrise.fragments.MyDayFragment;
import com.example.sunrise.fragments.ProfileFragment;
import com.example.sunrise.fragments.StatisticsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private MyDayFragment myDayFragment;
    private StatisticsFragment statisticsFragment;
    private CategoriesFragment categoriesFragment;
    private ProfileFragment profileFragment;
    private Chip priorityChip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomMenu();
        setupFabButton();
    }

    private void setupBottomMenu() {
        BottomNavigationView bottomNavView = findViewById(R.id.bottom_navigation);

        // Initiating fragments instances
        myDayFragment = new MyDayFragment();
        statisticsFragment = new StatisticsFragment();
        categoriesFragment = new CategoriesFragment();
        profileFragment = new ProfileFragment();

        // Setting listener for item pressed in bottom navigation bar
        bottomNavView.setOnItemSelectedListener(this::onItemSelectedListener);

        // Setting default page when app is opened
        bottomNavView.setSelectedItemId(R.id.page_my_day);
    }

    private boolean onItemSelectedListener(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.page_my_day) {
            showFragment(myDayFragment);
            return true;
        } else if (itemId == R.id.page_statistics) {
            showFragment(statisticsFragment);
            return true;
        } else if (itemId == R.id.page_categories) {
            showFragment(categoriesFragment);
            return true;
        } else if (itemId == R.id.page_profile) {
            showFragment(profileFragment);
            return true;
        }
        return false;
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragments_container, fragment)
                .commit();
    }

    private void setupFabButton() {
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
            View bottomSheetContentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.create_task_bottom_sheet, null);
            bottomSheetDialog.setContentView(bottomSheetContentView);
            bottomSheetDialog.show();

            TextInputLayout textInputLayout = bottomSheetContentView.findViewById(R.id.textFieldLayout);
            TextInputEditText editText = bottomSheetContentView.findViewById(R.id.title);
            Button createBtn = bottomSheetContentView.findViewById(R.id.create_btn);

            priorityChip = bottomSheetContentView.findViewById(R.id.priority);

            priorityChip.setOnClickListener(view -> {
                PopupMenu popup = new PopupMenu(this, view);
                popup.getMenuInflater().inflate(R.menu.priorities_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(this::onPriorityMenuItemClick);

                popup.show();
            });


            createBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Objects.requireNonNull(editText.getText()).toString().isEmpty()) {
                        textInputLayout.setError("Please type title");
                    } else {
                        // TO-DO Firebase save task to db

                        bottomSheetDialog.dismiss();
                    }
                }
            });

        });
    }

    private boolean onPriorityMenuItemClick(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.priority_high) {
            priorityChip.setText(R.string.priority_high);
            return true;
        } else if (itemId == R.id.priority_medium) {
            priorityChip.setText(R.string.priority_medium);
            return true;
        } else if (itemId == R.id.priority_low) {
            priorityChip.setText(R.string.priority_low);
            return true;
        } else if (itemId == R.id.priority_regular) {
            priorityChip.setText(R.string.priority_regular);
            return true;
        } else {
            return false;
        }
    }

    private String getPriorityValue(String selectedPriority) {
        // Check if the selected priority is the default one
        if (selectedPriority.equals(getString(R.string.priority))) {
            // If so, set the priority to the regular
            return getString(R.string.priority_regular);
        } else {
            // Otherwise, return the selected priority
            return selectedPriority;
        }
    }


}