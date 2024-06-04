package com.example.sunrise.models;

import java.util.List;

public class User {
    private String userId;
    private List<String> workspaceIds;
    private String profilePhotoUri;
    private String nickname;
    private String email;
    private long createdAt;
    private long updatedAt;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String userId, String nickname, String email, String profilePhotoUri) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.profilePhotoUri = profilePhotoUri;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis(); // Set updatedAt to the same value as createdAt initially
    }

    public User(String userId, List<String> workspaceIds, String profilePhotoUri, String nickname) {
        this.userId = userId;
        this.workspaceIds = workspaceIds;
        this.profilePhotoUri = profilePhotoUri;
        this.nickname = nickname;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis(); // Set updatedAt to the same value as createdAt initially
    }

    // Getter and setter for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and setter for workspaceIds
    public List<String> getWorkspaceIds() {
        return workspaceIds;
    }

    public void setWorkspaceIds(List<String> workspaceIds) {
        this.workspaceIds = workspaceIds;
    }

    // Getter and setter for profilePhotoUri
    public String getProfilePhotoUri() {
        return profilePhotoUri;
    }

    public void setProfilePhotoUri(String profilePhotoUri) {
        this.profilePhotoUri = profilePhotoUri;
    }

    // Getter and setter for nickname
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
