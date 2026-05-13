package com.omartitouhi.mindmate.data.model;

public class User {
    private String id;
    private String displayName;
    private String email;
    private long createdAt;

    public User() {
    }

    public User(String id, String displayName, String email, long createdAt) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
