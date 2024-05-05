package com.example.sunrise.services;

import com.example.sunrise.models.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CategoryService {

    private final DatabaseReference categoriesRef;

    public CategoryService() {
        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        categoriesRef = database.getReference("Categories");
    }

    /**
     * Method to save the category to Firebase database
     */
    public void saveCategory(Category category) {
        // Update the updatedAt timestamp before starting saving
        category.setUpdatedAt(System.currentTimeMillis());

        // Generate a reference to a new child location under "categories" with a client-side auto-generated key
        DatabaseReference newCategoryRef = categoriesRef.push();

        String categoryId = newCategoryRef.getKey(); // Retrieve the unique ID
        category.setCategoryId(categoryId); // Save this unique ID to the category object

        newCategoryRef.setValue(category); // Save the category to Firebase database
    }

    /**
     * Method to update the category in Firebase database
     */
    public void updateCategory(Category category) {
        // Update the updatedAt timestamp before starting saving
        category.setUpdatedAt(System.currentTimeMillis());

        // Get the reference to the category's location in Firebase using its unique ID
        DatabaseReference categoryRef = categoriesRef.child(category.getCategoryId());

        // Update the category at the specified location in Firebase
        categoryRef.setValue(category);
    }


    /**
     * Method to retrieve all categories of currently logged-in user
     */
    public void getCategories(ValueEventListener listener) {
        // Get currently logged-in user Id
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Create a query to filter categories by createdByUserId
        Query query = categoriesRef.orderByChild("createdByUserId").equalTo(userId);

        // Add ValueEventListener to the query to listen for changes in Firebase data
        query.addValueEventListener(listener);
    }
}

