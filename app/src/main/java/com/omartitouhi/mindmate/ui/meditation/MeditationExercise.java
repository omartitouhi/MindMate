package com.omartitouhi.mindmate.ui.meditation;

public class MeditationExercise {
    private final String title;
    private final String description;
    private final long durationMillis;

    public MeditationExercise(String title, String description, long durationMillis) {
        this.title = title;
        this.description = description;
        this.durationMillis = durationMillis;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getDurationMillis() {
        return durationMillis;
    }
}
