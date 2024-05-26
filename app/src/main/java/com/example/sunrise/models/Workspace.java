package com.example.sunrise.models;

import java.util.List;

public class Workspace {
    private String workspaceId;
    private String title;
    private List<String> workspaceAdminIds;
    private List<String> memberIds;
    private long createdAt;
    private long updatedAt;

    public Workspace(String workspaceId, String title, List<String> workspaceAdminIds, List<String> memberIds) {
        this.workspaceId = workspaceId;
        this.title = title;
        this.workspaceAdminIds = workspaceAdminIds;
        this.memberIds = memberIds;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis(); // Set updatedAt to the same value as createdAt initially
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getWorkspaceAdminIds() {
        return workspaceAdminIds;
    }

    public void setWorkspaceAdminIds(List<String> workspaceAdminIds) {
        this.workspaceAdminIds = workspaceAdminIds;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
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
