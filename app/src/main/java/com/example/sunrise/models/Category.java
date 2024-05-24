package com.example.sunrise.models;

public class Category {
    private String title;
    private int color;
    private String icon;
    private String defaultTagId;
    private String categoryId;
    private String createdByUserId;
    private long createdAt;
    private long updatedAt;

    public Category() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Category(String title, int color, String icon, String defaultTagId, String createdByUserId) {
        this.title = title;
        this.color = color;
        this.icon = icon;
        this.defaultTagId = defaultTagId;
        this.createdByUserId = createdByUserId;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis(); // Set updatedAt to the same value as createdAt initially
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
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

    public String getDefaultTagId() {
        return defaultTagId;
    }

    public void setDefaultTagId(String defaultTagId) {
        this.defaultTagId = defaultTagId;
    }
}
