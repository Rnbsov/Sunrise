package com.example.sunrise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.sunrise.fragments.CategoriesFragment;
import com.example.sunrise.fragments.MyDayFragment;
import com.example.sunrise.fragments.ProfileFragment;
import com.example.sunrise.fragments.StatisticsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private MyDayFragment myDayFragment;
    private StatisticsFragment statisticsFragment;
    private CategoriesFragment categoriesFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomMenu();
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

}