package com.omartitouhi.mindmate.data.model;

import com.google.gson.annotations.SerializedName;

public class AiAnalysisResult {
    @SerializedName("main_emotion")
    private String mainEmotion;

    @SerializedName("estimated_stress_level")
    private int estimatedStressLevel;

    @SerializedName("short_summary")
    private String shortSummary;

    @SerializedName("personalized_advice")
    private String personalizedAdvice;

    @SerializedName("exercise_suggestion")
    private String exerciseSuggestion;

    public AiAnalysisResult() {
    }

    public AiAnalysisResult(String mainEmotion, int estimatedStressLevel, String shortSummary, String personalizedAdvice, String exerciseSuggestion) {
        this.mainEmotion = mainEmotion;
        this.estimatedStressLevel = estimatedStressLevel;
        this.shortSummary = shortSummary;
        this.personalizedAdvice = personalizedAdvice;
        this.exerciseSuggestion = exerciseSuggestion;
    }

    public String getMainEmotion() {
        return mainEmotion;
    }

    public int getEstimatedStressLevel() {
        return estimatedStressLevel;
    }

    public String getShortSummary() {
        return shortSummary;
    }

    public String getPersonalizedAdvice() {
        return personalizedAdvice;
    }

    public String getExerciseSuggestion() {
        return exerciseSuggestion;
    }
}
