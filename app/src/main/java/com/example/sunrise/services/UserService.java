package com.example.sunrise.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sunrise.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {

    private final DatabaseReference usersRef;

    public UserService() {
        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");
    }

    /**
     * Method to save the tag to Firebase database
     */

    /**
     * Method to create a new user in Firebase database
     */
    public void createUser(User user) {
        // Generate a reference to a new child location under "Users" with a client-side auto-generated key
        DatabaseReference newUserRef = usersRef.push();

        String userId = newUserRef.getKey(); // Retrieve the unique ID
        user.setUserId(userId); // Save this unique ID to the user object

        newUserRef.setValue(user); // Save the tag to Firebase database

        // Save the user to Firebase database
        usersRef.child(user.getUserId()).setValue(user);
    }

    /**
     * Method to update an existing user in Firebase database
     */
    public void updateUser(User user) {
        // Update the updatedAt timestamp before starting saving
        user.setUpdatedAt(System.currentTimeMillis());

        // Get the reference to the user's location in Firebase using its unique ID
        DatabaseReference userRef = usersRef.child(user.getUserId());

        // Update the user at the specified location in Firebase
        userRef.setValue(user);
    }

    /**
     * Updates the profile picture URI of the user in Firebase database.
     * @param userId The ID of the user whose profile picture URI is to be updated.
     * @param profilePhotoUri The new profile picture URI.
     */
    public void updateProfilePictureUri(String userId, String profilePhotoUri) {
        // Get the reference to the user's location in Firebase using the user ID
        DatabaseReference userRef = usersRef.child(userId).child("profilePhotoUri");

        // Update the profile picture URI at the specified location in Firebase
        userRef.setValue(profilePhotoUri);
    }

    /**
     * Method to retrieve users by their IDs
     */
    public void retrieveUsersByIds(List<String> userIds, UsersRetrievedListener listener) {
        Map<String, User> usersMap = new HashMap<>();

        for (String userId : userIds) {
            DatabaseReference userRef = usersRef.child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        usersMap.put(userId, user);

                        if (usersMap.size() == userIds.size()) {
                            listener.onUsersRetrieved(usersMap);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("UserService", "Failed to retrieve users by IDs", error.toException());
                }
            });
        }
    }

    public interface UsersRetrievedListener {
        void onUsersRetrieved(Map<String, User> usersMap);
    }
}
