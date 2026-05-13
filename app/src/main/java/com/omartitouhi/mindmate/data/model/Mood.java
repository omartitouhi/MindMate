package com.omartitouhi.mindmate.data.model;

public class Mood {
    private String id;
    private String userId;
    private String mood;
    private int stressScore;
    private String note;
    private long createdAt;
    private String weatherCity;
    private Double weatherTemperature;
    private String weatherCondition;

    public Mood() {
    }

    public Mood(String id, String userId, String mood, int stressScore, String note, long createdAt) {
        this(id, userId, mood, stressScore, note, createdAt, null, null, null);
    }

    public Mood(String id, String userId, String mood, int stressScore, String note, long createdAt, String weatherCity, Double weatherTemperature, String weatherCondition) {
        this.id = id;
        this.userId = userId;
        this.mood = mood;
        this.stressScore = stressScore;
        this.note = note;
        this.createdAt = createdAt;
        this.weatherCity = weatherCity;
        this.weatherTemperature = weatherTemperature;
        this.weatherCondition = weatherCondition;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getMood() {
        return mood;
    }

    public int getStressScore() {
        return stressScore;
    }

    public String getNote() {
        return note;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getWeatherCity() {
        return weatherCity;
    }

    public Double getWeatherTemperature() {
        return weatherTemperature;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }
}
