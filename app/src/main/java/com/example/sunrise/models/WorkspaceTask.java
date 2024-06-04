package com.example.sunrise.models;

public class WorkspaceTask {
    private String workspaceId;
    private String status;
    private String title;
    private String priority;
    private String assignedUserId;
    private String taskId;
    private long createdAt;
    private long updatedAt;

    public WorkspaceTask() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public WorkspaceTask(String title, String status,String priority, String assignedUserId, String workspaceId) {
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.assignedUserId = assignedUserId;
        this.workspaceId = workspaceId;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis(); // Set updatedAt to the same value as createdAt initially
    }

    // Getters and Setters
    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(String assignedUserId) {
        this.assignedUserId = assignedUserId;
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
}
