package com.example.sunrise.models;

public class Tag {
    private String title;
    private int color;
    private String tagId;
    private String createdByUserId;
    private long createdAt;
    private long updatedAt;

    public Tag() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Tag(String title, int color, String createdByUserId) {
        this.title = title;
        this.color = color;
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

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
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
}
