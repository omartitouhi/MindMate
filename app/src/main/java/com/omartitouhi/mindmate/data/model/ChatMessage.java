package com.omartitouhi.mindmate.data.model;

public class ChatMessage {
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";

    private String id;
    private String userId;
    private String role;
    private String content;
    private long createdAt;

    public ChatMessage() {
    }

    public ChatMessage(String id, String userId, String role, String content, long createdAt) {
        this.id = id;
        this.userId = userId;
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
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

    public boolean isUserMessage() {
        return ROLE_USER.equals(role);
    }
}
