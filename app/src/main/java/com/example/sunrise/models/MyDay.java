package com.example.sunrise.models;

import java.util.ArrayList;
import java.util.List;

public class MyDay {
    private String userId;
    private List<String> myDayTaskIds;
    private long createdAt;
    private long updatedAt;

    public MyDay() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public MyDay(String userId) {
        this.userId = userId;
        this.myDayTaskIds = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis(); // Set updatedAt to the same value as createdAt initially
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getMyDayTaskIds() {
        return myDayTaskIds;
    }

    public void setMyDayTaskIds(List<String> myDayTaskIds) {
        this.myDayTaskIds = myDayTaskIds;
    }
}
