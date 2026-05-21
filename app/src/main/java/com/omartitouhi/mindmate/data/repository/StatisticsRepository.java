package com.omartitouhi.mindmate.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.data.local.AppDatabase;
import com.omartitouhi.mindmate.data.local.JournalEntity;
import com.omartitouhi.mindmate.data.local.MoodEntity;
import com.omartitouhi.mindmate.data.model.StatisticsSummary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsRepository {
    private final Context appContext;
    private final LiveData<List<MoodEntity>> moods;
    private final LiveData<List<JournalEntity>> journals;
    private final MediatorLiveData<StatisticsSummary> summary = new MediatorLiveData<>();
    private List<MoodEntity> latestMoods = new ArrayList<>();
    private List<JournalEntity> latestJournals = new ArrayList<>();

    public StatisticsRepository(Context context) {
        appContext = context.getApplicationContext();
        AppDatabase database = AppDatabase.getInstance(appContext);
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "anonymous";
        moods = database.moodDao().getMoodsForUser(userId);
        journals = database.journalDao().getJournalEntriesForUser(userId);

        summary.addSource(moods, moodEntities -> {
            latestMoods = moodEntities == null ? new ArrayList<>() : moodEntities;
            publishSummary();
        });
        summary.addSource(journals, journalEntities -> {
            latestJournals = journalEntities == null ? new ArrayList<>() : journalEntities;
            publishSummary();
        });
    }

    public LiveData<StatisticsSummary> getSummary() {
        return summary;
    }

    private void publishSummary() {
        boolean empty = latestMoods.isEmpty() && latestJournals.isEmpty();
        summary.setValue(new StatisticsSummary(
                empty,
                buildMoodScoresLastSevenDays(latestMoods),
                calculateAverageStress(latestMoods),
                latestJournals.size(),
                localizeMood(findMostFrequentMood(latestMoods)),
                calculateWeeklyProgress(latestMoods)
        ));
    }

    private List<Float> buildMoodScoresLastSevenDays(List<MoodEntity> moods) {
        List<Float> scores = new ArrayList<>();
        long startOfToday = startOfDay(System.currentTimeMillis());
        for (int i = 6; i >= 0; i--) {
            long dayStart = startOfToday - (i * 24L * 60L * 60L * 1000L);
            long dayEnd = dayStart + 24L * 60L * 60L * 1000L;
            float total = 0f;
            int count = 0;
            for (MoodEntity mood : moods) {
                if (mood.getCreatedAt() >= dayStart && mood.getCreatedAt() < dayEnd) {
                    total += moodToScore(mood.getMood());
                    count++;
                }
            }
            scores.add(count == 0 ? 0f : total / count);
        }
        return scores;
    }

    private float calculateAverageStress(List<MoodEntity> moods) {
        if (moods.isEmpty()) {
            return 0f;
        }
        int total = 0;
        for (MoodEntity mood : moods) {
            total += mood.getStressScore();
        }
        return total / (float) moods.size();
    }

    private String findMostFrequentMood(List<MoodEntity> moods) {
        if (moods.isEmpty()) {
            return "-";
        }
        Map<String, Integer> counts = new HashMap<>();
        for (MoodEntity mood : moods) {
            String moodName = mood.getMood();
            counts.put(moodName, counts.containsKey(moodName) ? counts.get(moodName) + 1 : 1);
        }

        String bestMood = "-";
        int bestCount = 0;
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > bestCount) {
                bestMood = entry.getKey();
                bestCount = entry.getValue();
            }
        }
        return bestMood;
    }

    private String calculateWeeklyProgress(List<MoodEntity> moods) {
        long now = System.currentTimeMillis();
        long sevenDaysAgo = now - 7L * 24L * 60L * 60L * 1000L;
        int moodCount = 0;
        float stressTotal = 0f;
        for (MoodEntity mood : moods) {
            if (mood.getCreatedAt() >= sevenDaysAgo) {
                moodCount++;
                stressTotal += mood.getStressScore();
            }
        }
        if (moodCount == 0) {
            return appContext.getString(R.string.statistics_no_data_week);
        }
        float weeklyStress = stressTotal / moodCount;
        if (weeklyStress <= 4f) {
            return appContext.getString(R.string.statistics_week_stable);
        }
        if (weeklyStress <= 7f) {
            return appContext.getString(R.string.statistics_week_watch);
        }
        return appContext.getString(R.string.statistics_week_high_stress);
    }

    private float moodToScore(String mood) {
        if ("Happy".equals(mood) || appContext.getString(R.string.mood_value_happy).equals(mood)) {
            return 5f;
        }
        if ("Calm".equals(mood) || appContext.getString(R.string.mood_value_calm).equals(mood)) {
            return 4f;
        }
        if ("Tired".equals(mood) || appContext.getString(R.string.mood_value_tired).equals(mood)) {
            return 3f;
        }
        if ("Sad".equals(mood) || "Stressed".equals(mood)
                || appContext.getString(R.string.mood_value_sad).equals(mood)
                || appContext.getString(R.string.mood_value_stressed).equals(mood)) {
            return 2f;
        }
        if ("Angry".equals(mood) || appContext.getString(R.string.mood_value_angry).equals(mood)) {
            return 1f;
        }
        return 3f;
    }

    private String localizeMood(String mood) {
        if ("Happy".equals(mood)) {
            return appContext.getString(R.string.mood_value_happy);
        }
        if ("Calm".equals(mood)) {
            return appContext.getString(R.string.mood_value_calm);
        }
        if ("Tired".equals(mood)) {
            return appContext.getString(R.string.mood_value_tired);
        }
        if ("Sad".equals(mood)) {
            return appContext.getString(R.string.mood_value_sad);
        }
        if ("Stressed".equals(mood)) {
            return appContext.getString(R.string.mood_value_stressed);
        }
        if ("Angry".equals(mood)) {
            return appContext.getString(R.string.mood_value_angry);
        }
        return mood;
    }

    private long startOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
