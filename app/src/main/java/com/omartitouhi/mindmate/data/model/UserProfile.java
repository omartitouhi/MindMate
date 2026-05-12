package com.omartitouhi.mindmate.data.model;

public class UserProfile {
    private String id;
    private String displayName;
    private String email;

    public UserProfile() {
    }

    public UserProfile(String id, String displayName, String email) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
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
}
