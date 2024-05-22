package com.example.sunrise.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.sunrise.helpers.CategoryCreationHelper;
import com.example.sunrise.models.Category;
import com.example.sunrise.services.CategoryService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;
    private View fragment;
    private CategoryService categoryService;
    private CategoryCreationHelper categoryCreationHelper;
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
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView tagsTile = fragment.findViewById(R.id.tags_tile);
        CardView completedTasksTile = fragment.findViewById(R.id.completed_tile);

        tagsTile.setOnClickListener(v -> {
            // Get the NavController
            NavController navController = Navigation.findNavController(view);

            // Navigating to tagsFragment
            navController.navigate(R.id.action_page_categories_to_tagsFragment);
        });

        completedTasksTile.setOnClickListener(v -> {
            // Get the NavController
            NavController navController = Navigation.findNavController(view);

            // Navigating to tagsFragment
            navController.navigate(R.id.action_page_categories_to_completedTasksFragment);
        });

        // Setup RecyclerView
        setupRecyclerView();

        // Initialize CategoryService to interact with Firebase database
        categoryService = new CategoryService();

        // Initialize CategoryCreationHelper
        categoryCreationHelper = new CategoryCreationHelper(requireContext());

        // Fetch categories
        fetchCategoriesFromDatabase();
    }

    private void setupRecyclerView() {
        // Initialize RecyclerView and adapter
        categoriesRecyclerView = fragment.findViewById(R.id.categories_recycler_view);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this::onCategoryClick, this::onCategoryAddButtonClick);

        // Set adapter to RecyclerView
        categoriesRecyclerView.setAdapter(categoryAdapter);
    }

    private void fetchCategoriesFromDatabase() {
        ValueEventListener categoriesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Category> categoryList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    categoryList.add(category);
                }

                // Updating categoryList in adapter
                categoryAdapter.setCategories(categoryList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e("CategoriesFragment", "Error fetching categories", databaseError.toException());
            }
        };

        // Call getCategories method from CategoryService to register the listener
        categoryService.getCategories(categoriesListener);
    }

    /**
     * Handle category item click
     *
     * @param category clicked category object
     */
    private void onCategoryClick(Category category) {
        Bundle bundle = new Bundle();
        bundle.putString("categoryId", category.getCategoryId());
        bundle.putString("categoryTitle", category.getTitle());

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_page_categories_to_categoryFragment, bundle);
    }

    private void onCategoryAddButtonClick(View v) {
        categoryCreationHelper.showCategoryCreationDialog((ViewGroup) requireView());
    }
}
