package com.omartitouhi.mindmate.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {MoodEntity.class, JournalEntity.class}, version = 5, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;
    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE journal_entries ADD COLUMN pendingDelete INTEGER NOT NULL DEFAULT 0");
        }
    };

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
                    ).addMigrations(MIGRATION_4_5).build();
                }
            }
        }
        return instance;
    }
}
