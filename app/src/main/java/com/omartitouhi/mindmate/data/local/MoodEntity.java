package com.omartitouhi.mindmate.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.omartitouhi.mindmate.data.model.Mood;

@Entity(tableName = "moods")
public class MoodEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String userId;
    private String mood;
    private int stressScore;
    private String note;
    private long createdAt;
    private boolean synced;

    public MoodEntity(@NonNull String id, String userId, String mood, int stressScore, String note, long createdAt, boolean synced) {
        this.id = id;
        this.userId = userId;
        this.mood = mood;
        this.stressScore = stressScore;
        this.note = note;
        this.createdAt = createdAt;
        this.synced = synced;
    }

    public static MoodEntity fromMood(Mood mood, boolean synced) {
        return new MoodEntity(
                mood.getId(),
                mood.getUserId(),
                mood.getMood(),
                mood.getStressScore(),
                mood.getNote(),
                mood.getCreatedAt(),
                synced
        );
    }

    public Mood toMood() {
        return new Mood(id, userId, mood, stressScore, note, createdAt);
    }

    @NonNull
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

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }
}
