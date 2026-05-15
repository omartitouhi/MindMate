package com.omartitouhi.mindmate.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface JournalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(JournalEntity journalEntity);

    @Update
    void update(JournalEntity journalEntity);

    @Delete
    void delete(JournalEntity journalEntity);

    @Query("DELETE FROM journal_entries WHERE id = :id")
    void deleteById(String id);

    @Query("UPDATE journal_entries SET synced = 1 WHERE id = :id")
    void markAsSynced(String id);

    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    LiveData<List<JournalEntity>> getAllJournalEntries();

    @Query("SELECT * FROM journal_entries WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<JournalEntity>> getJournalEntriesForUser(String userId);

    @Query("SELECT * FROM journal_entries WHERE id = :id LIMIT 1")
    LiveData<JournalEntity> getJournalEntry(String id);

    @Query("SELECT * FROM journal_entries WHERE userId = :userId AND synced = 0 ORDER BY updatedAt ASC")
    List<JournalEntity> getUnsyncedJournalEntriesForUser(String userId);
}
