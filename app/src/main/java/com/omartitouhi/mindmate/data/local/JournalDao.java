package com.omartitouhi.mindmate.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.omartitouhi.mindmate.data.model.JournalEntry;

import java.util.List;

@Dao
public interface JournalDao {
    @Insert
    void insert(JournalEntry journalEntry);

    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    LiveData<List<JournalEntry>> getAllJournalEntries();
}
