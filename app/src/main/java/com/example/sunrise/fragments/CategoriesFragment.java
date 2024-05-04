package com.example.sunrise.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sunrise.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class CategoriesFragment extends Fragment {

    private View fragment;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment = inflater.inflate(R.layout.fragment_categories, container, false);
        return fragment;}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView tagsTile = fragment.findViewById(R.id.tags_tile);

        tagsTile.setOnClickListener(v -> {
            // Get the NavController
            NavController navController = Navigation.findNavController(view);

            // Navigating to tagsFragment
            navController.navigate(R.id.action_page_categories_to_tagsFragment);
        });

    }
}