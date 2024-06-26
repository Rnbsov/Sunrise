package com.example.sunrise.models;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private String title;
    private String priority;
    private String taskId;
    private List<String> tags;
    private String categoryId;
    private long createdAt;
    private long updatedAt;
    private String createdByUserId;
    private boolean isCompleted;
    private long completedAt;

    public Task() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Task(String title, String priority, List<String> tags, String categoryId, String createdByUserId) {
        this.title = title;
        this.priority = priority;
        this.tags = tags;
        this.categoryId = categoryId;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis(); // Set updatedAt to the same value as createdAt initially
        this.createdByUserId = createdByUserId;
    }

    public Task(Task task) {
        this.title = task.title;
        this.priority = task.priority;
        this.tags = new ArrayList<>(task.tags);
        this.categoryId = task.categoryId;
        this.createdAt = task.createdAt;
        this.updatedAt = task.updatedAt;
        this.createdByUserId = task.createdByUserId;
        this.taskId = task.taskId;
        this.isCompleted = task.isCompleted;
        this.completedAt = task.completedAt;
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

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(long completedAt) {
        this.completedAt = completedAt;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
