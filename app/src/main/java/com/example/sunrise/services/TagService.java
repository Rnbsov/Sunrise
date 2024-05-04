package com.example.sunrise.services;

import com.example.sunrise.models.Tag;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class TagService {

    private final DatabaseReference tagsRef;

    public TagService() {
        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tagsRef = database.getReference("Tags");
    }

    /**
     * Method to save the tag to Firebase database
     */
    public void saveTag(Tag tag) {
        // Update the updatedAt timestamp before starting saving
        tag.setUpdatedAt(System.currentTimeMillis());

        // Generate a reference to a new child location under "Tags" with a client-side auto-generated key
        DatabaseReference newTagRef = tagsRef.push();

        String tagId = newTagRef.getKey(); // Retrieve the unique ID
        tag.setTagId(tagId); // Save this unique ID to the tag object

        newTagRef.setValue(tag); // Save the tag to Firebase database
    }

    /**
     * Method to update the tag in Firebase database
     */
    public void updateTag(Tag tag) {
        // Update the updatedAt timestamp before starting saving
        tag.setUpdatedAt(System.currentTimeMillis());

        // Get the reference to the tag's location in Firebase using its unique ID
        DatabaseReference tagRef = tagsRef.child(tag.getTagId());

        // Update the tag at the specified location in Firebase
        tagRef.setValue(tag);
    }

    /**
     * Method to retrieve all tasks of currently logged-in user
     */
    public void getTags(ValueEventListener listener) {
        // Get currently logged-in user Id
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Create a query to filter tags by createdByUserId
        Query query = tagsRef.orderByChild("createdByUserId").equalTo(userId);

        // Add ValueEventListener to the query to listen for changes in Firebase data
        query.addValueEventListener(listener);
    }
}
