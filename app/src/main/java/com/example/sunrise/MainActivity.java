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

        if (itemId == R.id.priority_1) {
            priorityChip.setText(R.string.p1);
            return true;
        } else if (itemId == R.id.priority_2) {
            priorityChip.setText(R.string.p2);
            return true;
        } else if (itemId == R.id.priority_3) {
            priorityChip.setText(R.string.p3);
            return true;
        } else if (itemId == R.id.priority_4) {
            priorityChip.setText(R.string.p4);
            return true;
        } else {
            return false;
        }
    }

}