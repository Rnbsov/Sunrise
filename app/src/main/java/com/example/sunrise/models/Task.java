package com.example.sunrise.models;

public class Task {
    private  String title;
    private  String priority;

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
}
