package com.example.sunrise.services;

import com.example.sunrise.models.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
}
