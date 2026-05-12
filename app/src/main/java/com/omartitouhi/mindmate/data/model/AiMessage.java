package com.omartitouhi.mindmate.data.model;

public class AiMessage {
    private String role;
    private String content;
    private long createdAt;

    public AiMessage() {
    }

    public AiMessage(String role, String content, long createdAt) {
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
