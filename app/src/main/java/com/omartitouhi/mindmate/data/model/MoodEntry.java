package com.omartitouhi.mindmate.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mood_entries")
public class MoodEntry {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private int score;
    private String note;
    private long createdAt;

    public MoodEntry(int score, String note, long createdAt) {
        this.score = score;
        this.note = note;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public String getNote() {
        return note;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
