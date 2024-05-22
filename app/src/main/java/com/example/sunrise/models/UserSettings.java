package com.example.sunrise.models;

public class UserSettings {
    private String id;
    private String createdByUserId;
    private String defaultCategoryId;
    private long createdAt;
    private long updatedAt;

    public UserSettings() {}

    public UserSettings(String createdByUserId, String defaultCategoryId) {
        this.createdByUserId = createdByUserId;
        this.defaultCategoryId = defaultCategoryId;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis(); // Set updatedAt to the same value as createdAt initially
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getDefaultCategoryId() {
        return defaultCategoryId;
    }

    public void setDefaultCategoryId(String defaultCategoryId) {
        this.defaultCategoryId = defaultCategoryId;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
