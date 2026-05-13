package com.omartitouhi.mindmate.data.model;

public class Mood {
    private String id;
    private String userId;
    private String mood;
    private int stressScore;
    private String note;
    private long createdAt;

    public Mood() {
    }

    public Mood(String id, String userId, String mood, int stressScore, String note, long createdAt) {
        this.id = id;
        this.userId = userId;
        this.mood = mood;
        this.stressScore = stressScore;
        this.note = note;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getMood() {
        return mood;
    }

    public int getStressScore() {
        return stressScore;
    }

    public String getNote() {
        return note;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
