package com.example.sunrise.services;

import com.example.sunrise.models.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class TagService {

    private final DatabaseReference tasksRef;

    public TagService() {
        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tasksRef = database.getReference("Tags");
    }

    /**
     * Method to save the tag to Firebase database
     */
    public void saveTask(Task task) {
        // Update the updatedAt timestamp before starting saving
        task.setUpdatedAt(System.currentTimeMillis());

        // Generate a reference to a new child location under "Tags" with a client-side auto-generated key
        DatabaseReference newTaskRef = tasksRef.push();

        String taskId = newTaskRef.getKey(); // Retrieve the unique ID
        task.setTaskId(taskId); // Save this unique ID to the tag object

        newTaskRef.setValue(task); // Save the tag to Firebase database
    }

    /**
     * Method to update the tag in Firebase database
     */
    public void updateTask(Task task) {
        // Update the updatedAt timestamp before starting saving
        task.setUpdatedAt(System.currentTimeMillis());

        // Get the reference to the tag's location in Firebase using its unique ID
        DatabaseReference taskRef = tasksRef.child(task.getTaskId());

        // Update the tag at the specified location in Firebase
        taskRef.setValue(task);
    }

    /**
     * Method to retrieve all tasks of currently logged-in user
     */
    public void getTasks(ValueEventListener listener) {
        // Get currently logged-in user Id
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Create a query to filter tags by createdByUserId
        Query query = tasksRef.orderByChild("createdByUserId").equalTo(userId);

        // Add ValueEventListener to the query to listen for changes in Firebase data
        query.addValueEventListener(listener);
    }
}
