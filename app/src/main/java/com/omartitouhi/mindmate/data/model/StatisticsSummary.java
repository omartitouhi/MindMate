package com.omartitouhi.mindmate.data.model;

import java.util.List;

public class StatisticsSummary {
    private final boolean empty;
    private final List<Float> moodScoresLastSevenDays;
    private final float averageStress;
    private final int journalCount;
    private final String mostFrequentMood;
    private final String weeklyProgress;

    public StatisticsSummary(boolean empty, List<Float> moodScoresLastSevenDays, float averageStress, int journalCount, String mostFrequentMood, String weeklyProgress) {
        this.empty = empty;
        this.moodScoresLastSevenDays = moodScoresLastSevenDays;
        this.averageStress = averageStress;
        this.journalCount = journalCount;
        this.mostFrequentMood = mostFrequentMood;
        this.weeklyProgress = weeklyProgress;
    }

    public boolean isEmpty() {
        return empty;
    }

    public List<Float> getMoodScoresLastSevenDays() {
        return moodScoresLastSevenDays;
    }

    public float getAverageStress() {
        return averageStress;
    }

    public int getJournalCount() {
        return journalCount;
    }

    public String getMostFrequentMood() {
        return mostFrequentMood;
    }

    public String getWeeklyProgress() {
        return weeklyProgress;
    }
}
