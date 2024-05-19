package com.example.sunrise.services;

import com.example.sunrise.models.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class TaskService {

    private final DatabaseReference tasksRef;

    public TaskService() {
        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tasksRef = database.getReference("Tasks");
    }

    /**
     * Method to save the task to Firebase database
     */
    public void saveTask(Task task) {
        // Update the updatedAt timestamp before starting saving
        task.setUpdatedAt(System.currentTimeMillis());

        // Generate a reference to a new child location under "tasks" with a client-side auto-generated key
        DatabaseReference newTaskRef = tasksRef.push();

        String taskId = newTaskRef.getKey(); // Retrieve the unique ID
        task.setTaskId(taskId); // Save this unique ID to the task object

        newTaskRef.setValue(task); // Save the task to Firebase database
    }

    /**
     * Method to update the task in Firebase database
     */
    public void updateTask(Task task) {
        // Update the updatedAt timestamp before starting saving
        task.setUpdatedAt(System.currentTimeMillis());

        // Get the reference to the task's location in Firebase using its unique ID
        DatabaseReference taskRef = tasksRef.child(task.getTaskId());

        // Update the task at the specified location in Firebase
        taskRef.setValue(task);
    }


    /**
     * Method to retrieve all tasks of currently logged-in user
     */
    public void getTasks(ValueEventListener listener) {
        // Get currently logged-in user Id
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Create a query to filter tasks by createdByUserId
        Query query = tasksRef.orderByChild("createdByUserId").equalTo(userId);

        // Add ValueEventListener to the query to listen for changes in Firebase data
        query.addValueEventListener(listener);
    }

    /**
     * Method to retrieve tasks by category Id
     *
     * @apiNote Does not check for createdByUserId of currently logged-in user, because category ids are quite unique
     */
    public void getTasksByCategoryId(String categoryId, ValueEventListener listener) {
        // Create a query to filter tasks by categoryId
        Query query = tasksRef.orderByChild("categoryId").equalTo(categoryId);

        // Add ValueEventListener to the query to listen for changes in Firebase data
        query.addValueEventListener(listener);
    }
}
