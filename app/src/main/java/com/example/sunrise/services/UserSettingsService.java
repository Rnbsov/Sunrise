package com.example.sunrise.services;

import com.example.sunrise.models.UserSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserSettingsService {
    private final DatabaseReference userSettingsRef;

    public UserSettingsService() {
        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.userSettingsRef = database.getReference("UserSettings");
    }

    public void getUserSettings(ValueEventListener listener) {
        // Get currently logged-in user Id
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Create a query to get user's settings by userId
        Query query = userSettingsRef.orderByChild("createdByUserId").equalTo(userId);

        // Add SingleValueEventListener to the query to retrieve settings object once
        query.addListenerForSingleValueEvent(listener);
    }

    public void createUserSettings(UserSettings userSettings, OnCompleteListener<Void> onCompleteListener) {
        // Generate a reference to a new child location under "UserSettings" with a client-side auto-generated key
        DatabaseReference newUserSettingsRef = userSettingsRef.push();

        String userSettingsId = newUserSettingsRef.getKey(); // Retrieve the unique ID
        userSettings.setId(userSettingsId); // Save this unique ID to the UserSettings object

        // Save the UserSettings to Firebase database and attach the provided OnCompleteListener
        newUserSettingsRef.setValue(userSettings).addOnCompleteListener(onCompleteListener);
    }
}
