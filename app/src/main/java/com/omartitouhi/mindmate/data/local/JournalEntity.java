package com.omartitouhi.mindmate.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.omartitouhi.mindmate.data.model.JournalEntry;

@Entity(tableName = "journal_entries")
public class JournalEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String userId;
    private String title;
    private String content;
    private String mood;
    private long createdAt;
    private long updatedAt;
    private boolean synced;

    public JournalEntity(@NonNull String id, String userId, String title, String content, String mood, long createdAt, long updatedAt, boolean synced) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.mood = mood;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.synced = synced;
    }

    public static JournalEntity fromJournalEntry(JournalEntry entry, boolean synced) {
        return new JournalEntity(
                entry.getId(),
                entry.getUserId(),
                entry.getTitle(),
                entry.getContent(),
                entry.getMood(),
                entry.getCreatedAt(),
                entry.getUpdatedAt(),
                synced
        );
    }

    public JournalEntry toJournalEntry() {
        return new JournalEntry(id, userId, title, content, mood, createdAt, updatedAt);
    }

    @NonNull
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

    public boolean isSynced() {
        return synced;
    }
}
