package com.omartitouhi.mindmate.data.model;

public class JournalEntry {
    private String id;
    private String userId;
    private String title;
    private String content;
    private String mood;
    private long createdAt;
    private long updatedAt;

    public JournalEntry() {
    }

    public JournalEntry(String id, String userId, String title, String content, String mood, long createdAt, long updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.mood = mood;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getMood() {
        return mood;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }
}
