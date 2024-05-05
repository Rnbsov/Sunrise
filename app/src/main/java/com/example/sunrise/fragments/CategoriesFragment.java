package com.example.sunrise.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.adapters.CategoryAdapter;
import com.example.sunrise.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;
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

        // Initialize RecyclerView and adapter
        categoriesRecyclerView = fragment.findViewById(R.id.categories_recycler_view);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                // Handle category item click
                Toast.makeText(requireContext(), "Clicked on category: " + category.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set adapter to RecyclerView
        categoriesRecyclerView.setAdapter(categoryAdapter);

        // Set categories to adapter

// Get the resource ID of the drawable
        int drawableResourceId = R.drawable.account_circle_24px;
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Category 1", Color.RED, drawableResourceId, "lalalal"));
        categories.add(new Category("Category 2", Color.BLUE, drawableResourceId, "lalalal"));

         categoryAdapter.setCategories(categories);

    }
}