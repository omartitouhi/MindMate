package com.omartitouhi.mindmate.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MoodEntity moodEntity);

    @Query("UPDATE moods SET synced = 1 WHERE id = :id")
    void markAsSynced(String id);

    @Query("SELECT * FROM moods ORDER BY createdAt DESC")
    LiveData<List<MoodEntity>> getAllMoods();
}
