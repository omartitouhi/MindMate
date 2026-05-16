package com.omartitouhi.mindmate.data.model;

public class ChatMessage {
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";

    private String id;
    private String userId;
    private String role;
    private String content;
    private long timestamp;
    private String status;
    private long createdAt;

    public ChatMessage() {
    }

    public ChatMessage(String id, String userId, String role, String content, long createdAt) {
        this(id, userId, role, content, createdAt, "sent");
    }

    public ChatMessage(String id, String userId, String role, String content, long timestamp, String status) {
        this.id = id;
        this.userId = userId;
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
        this.createdAt = timestamp;
        this.status = status;
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
        return timestamp > 0 ? timestamp : createdAt;
    }

    public long getTimestamp() {
        return timestamp > 0 ? timestamp : createdAt;
    }

    public String getStatus() {
        return status == null ? "sent" : status;
    }

    public boolean isUserMessage() {
        return ROLE_USER.equals(role);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        this.createdAt = timestamp;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
        if (this.timestamp == 0) {
            this.timestamp = createdAt;
        }
    }
}
