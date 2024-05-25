package com.example.sunrise.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sunrise.models.Tag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * Method to save the category to Firebase database
     */
    public void saveTag(Tag tag, OnCompleteListener<Void> listener) {
        // Generate a reference to a new child location under "Tags" with a client-side auto-generated key
        DatabaseReference newTagRef = tagsRef.push();

        String tagId = newTagRef.getKey(); // Retrieve the unique ID
        tag.setTagId(tagId); // Save this unique ID to the tag object

        newTagRef.setValue(tag)
                .addOnCompleteListener(listener); // Save the tag to Firebase database and invoke the listener
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

    /**
     * Method to retrieve tag titles for a list of tag IDs
     */
    public void retrieveTagColorsByTagIds(List<String> tagIds, TagColorsListener listener) {
        // Create a map to store tag colors corresponding to their IDs
        Map<String, Integer> tagColorsMap = new HashMap<>();

        // Iterate through the list of tag IDs
        for (String tagId : tagIds) {
            DatabaseReference tagRef = tagsRef.child(tagId);
            tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve the tag object
                        int color = dataSnapshot.child("color").getValue(Integer.class);
                        tagColorsMap.put(tagId, color);

                        // Check if all tag titles are retrieved
                        if (tagColorsMap.size() == tagIds.size()) {
                            // Convert the map of tag colors to a list
                            List<Integer> tagColors = new ArrayList<>(tagColorsMap.values());

                            // Pass the list of tag colors to the listener
                            listener.onTagColorsRetrieved(tagColors);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("TagService", "Failed to retrieve colors by ids");
                }
            });
        }
    }

    public interface TagColorsListener {
        void onTagColorsRetrieved(List<Integer> tagColors);
    }

    /**
     * Method to retrieve tag titles for a list of tag IDs
     */
    public void retrieveTagTitlesByTagIds(List<String> tagIds, TagTitlesListener listener) {
        Map<String, String> tagTitlesMap = new HashMap<>();

        for (String tagId : tagIds) {
            DatabaseReference tagRef = tagsRef.child(tagId);
            tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String title = dataSnapshot.child("title").getValue(String.class);
                        tagTitlesMap.put(tagId, title);

                        if (tagTitlesMap.size() == tagIds.size()) {
                            listener.onTagTitlesRetrieved(tagTitlesMap);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("TagService", "Failed to retrieve titles by ids");
                }
            });
        }
    }

    public interface TagTitlesListener {
        void onTagTitlesRetrieved(Map<String, String> tagTitles);
    }
}
