package com.example.sunrise.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sunrise.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final DatabaseReference usersRef;

    public UserService() {
        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");
    }

    /**
     * Method to create a new user in Firebase database
     */
    public void createUser(User user) {
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
     * Method to get users by their IDs
     */
    public void getUsersByIds(List<String> userIds, UsersListener listener) {
        List<User> users = new ArrayList<>();

        for (String userId : userIds) {
            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        users.add(user);
                    }

                    if (users.size() == userIds.size()) {
                        listener.onUsersRetrieved(users);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("UserService", "Failed to retrieve users by IDs", databaseError.toException());
                }
            });
        }
    }

    public interface UsersListener {
        void onUsersRetrieved(List<User> users);
    }

    /**
     * Method to retrieve the username by user ID
     */
    public void getUsername(String userId, UserNameListener listener) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    listener.onUserNameFetched(user.getNickname());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserService", "Failed to find username", databaseError.toException());
            }
        });
    }

    public interface UserNameListener {
        void onUserNameFetched(String userName);
    }


    /**
     * Method to retrieve the current user's details
     */
    public void getCurrentUser(CurrentUserListener listener) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();

            DatabaseReference userRef = usersRef.child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        listener.onCurrentUserRetrieved(user);
                    } else {
                        listener.onCurrentUserRetrieved(null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    listener.onCancelled(error);
                }
            });
        } else {
            listener.onCurrentUserRetrieved(null);
        }
    }

    public interface CurrentUserListener {
        void onCurrentUserRetrieved(User user);
        void onCancelled(DatabaseError databaseError);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId   The ID of the user to retrieve.
     * @param listener The listener to handle the retrieved user.
     */
    public void getUserById(String userId, UserRetrievedListener listener) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                listener.onUserRetrieved(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserService", "Failed to find user", databaseError.toException());
            }
        });
    }

    public interface UserRetrievedListener {
        void onUserRetrieved(User user);
    }
}
