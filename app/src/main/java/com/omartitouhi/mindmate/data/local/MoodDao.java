package com.omartitouhi.mindmate.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.omartitouhi.mindmate.data.model.MoodEntry;

import java.util.List;

@Dao
public interface MoodDao {
    @Insert
    void insert(MoodEntry moodEntry);

    @Query("SELECT * FROM mood_entries ORDER BY createdAt DESC")
    LiveData<List<MoodEntry>> getAllMoodEntries();
}
