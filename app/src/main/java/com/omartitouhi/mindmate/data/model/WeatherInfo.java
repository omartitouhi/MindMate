package com.omartitouhi.mindmate.data.model;

public class WeatherInfo {
    private final String city;
    private final double temperature;
    private final String condition;
    private final String advice;

    public WeatherInfo(String city, double temperature, String condition, String advice) {
        this.city = city;
        this.temperature = temperature;
        this.condition = condition;
        this.advice = advice;
    }

    public String getCity() {
        return city;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getCondition() {
        return condition;
    }

    public String getAdvice() {
        return advice;
    }
}
