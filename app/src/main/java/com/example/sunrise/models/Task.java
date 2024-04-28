package com.example.sunrise.models;

import android.util.Log;

import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Task {
    private String title;
    private String priority;
    private String taskId;

    public Task() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Task(String title, String priority) {
        this.title = title;
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * Method to save the task to Firebase database
     */
    public void saveToFirebase() {
        // Get reference to Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tasksRef = database.getReference("Tasks");

        // Generate a reference to a new child location under "tasks" with an client-side auto-generated key
        DatabaseReference newTaskRef = tasksRef.push();

        taskId = newTaskRef.getKey(); // Retrieve the unique ID

        this.setTaskId(taskId); // Save this uniqueId to task object

        newTaskRef.setValue(this); // Save the task to Firebase database
    }
}