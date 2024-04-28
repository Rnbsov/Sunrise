package com.example.sunrise.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Task {
    private String title;
    private String priority;
    private String taskId;
    private long createdAt;
    private long updatedAt;
    private String createdByUserId;

    public Task() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Task(String title, String priority, String createdByUserId) {
        this.title = title;
        this.priority = priority;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis(); // Set updatedAt to the same value as createdAt initially
        this.createdByUserId = createdByUserId;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Method to save the task to Firebase database
     */
    public void saveToFirebase() {
        // Update the updatedAt timestamp before starting saving
        this.updatedAt = System.currentTimeMillis();

        // Get reference to Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tasksRef = database.getReference("Tasks");

        // Generate a reference to a new child location under "tasks" with an client-side auto-generated key
        DatabaseReference newTaskRef = tasksRef.push();

        taskId = newTaskRef.getKey(); // Retrieve the unique ID

        this.setTaskId(taskId); // Save this uniqueId to task object

        newTaskRef.setValue(this); // Save the task to Firebase database
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}