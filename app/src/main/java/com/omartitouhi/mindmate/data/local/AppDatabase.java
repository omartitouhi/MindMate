package com.omartitouhi.mindmate.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.omartitouhi.mindmate.data.model.JournalEntry;
import com.omartitouhi.mindmate.data.model.MoodEntry;

@Database(entities = {MoodEntry.class, JournalEntry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract MoodDao moodDao();

    public abstract JournalDao journalDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "mindmate.db"
                    ).build();
                }
            }
        }
        return instance;
    }
}
