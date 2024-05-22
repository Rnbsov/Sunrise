package com.example.sunrise.utils;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.example.sunrise.R;
import com.example.sunrise.models.Category;
import com.example.sunrise.services.CategoryService;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TaskUtils {

    public static void showPriorityPopupMenu(Context context, View anchorView, Chip priorityChip) {
        PopupMenu popup = new PopupMenu(context, anchorView);
        popup.getMenuInflater().inflate(R.menu.priorities_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            // Call onPriorityMenuItemClick with the MenuItem and priorityChip
            return TaskUtils.onPriorityMenuItemClick(item, priorityChip);
        });

        popup.show();
    }

    public static boolean onPriorityMenuItemClick(@NonNull MenuItem item, Chip priorityChip) {
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

    public static String getPriorityValue(String selectedPriority, String defaultPriority, String regularPriority) {
        // Check if the selected priority is the default one
        if (selectedPriority.equals(defaultPriority)) {
            // If so, set the priority to the regular
            return regularPriority;
        } else {
            // Otherwise, return the selected priority
            return selectedPriority;
        }
    }

    public static void showCategoriesPopupMenu(Context context, View v, Chip categoryChip, CategoryService categoryService, TaskUtils.OnMenuItemCategorySelected callback) {
        PopupMenu popup = new PopupMenu(context, categoryChip);

        // Retrieve categories from the database
        retrieveCategoriesFromDatabase(categoryService, categoryIdMap -> {
            // Add categories to the popup menu
            for (Map.Entry<Integer, Pair<String, String>> entry : categoryIdMap.entrySet()) {
                popup.getMenu().add(0, entry.getKey(), 0, entry.getValue().second);
            }

            // Show popupMenu after all asynchronous stuff is done
            popup.show();

            popup.setOnMenuItemClickListener(item -> {
                // Handle category selection
                int categoryIdHashCode = item.getItemId();

                Pair<String, String> selectedCategoryInfo = categoryIdMap.get(categoryIdHashCode); // Retrieve Pair using hash code
                assert selectedCategoryInfo != null;

                String selectedCategoryId = selectedCategoryInfo.first;
                String selectedCategoryName = selectedCategoryInfo.second;

                callback.onCategorySelected(selectedCategoryId, selectedCategoryName);

                return true;
            });
        });
    }

    private static void retrieveCategoriesFromDatabase(CategoryService categoryService, TaskUtils.CategoriesRetrievedCallback callback) {
        Map<Integer, Pair<String, String>> categoryIdMap = new HashMap<>(); // Map to store category IDs and their hash codes
        categoryService.getCategories(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Add categories to the categoryIdMap
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    if (category != null) {
                        String categoryId = category.getCategoryId();
                        String categoryName = category.getTitle();
                        int categoryIdHashCode = categoryId.hashCode();  // Convert categoryId to hashcode cause PopupMenu add item method needs int as id
                        categoryIdMap.put(categoryIdHashCode, new Pair<>(categoryId, categoryName)); // Store the relationship between hash code and Pair of categoryId/categoryName
                    }
                }

                // Invoke the callback with passed categoryIdMap
                callback.onCategoriesRetrieved(categoryIdMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CategoriesFragment", "Getting categories failed");
            }
        });
    }

    interface CategoriesRetrievedCallback {
        void onCategoriesRetrieved(Map<Integer, Pair<String, String>> categoryIdMap);
    }
    public interface OnMenuItemCategorySelected {
        void onCategorySelected(String categoryId, String categoryName);
    }
}
