package com.omartitouhi.mindmate.data.remote;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("current_weather")
    private CurrentWeather currentWeather;

    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public static class CurrentWeather {
        @SerializedName("temperature")
        private double temperature;

        @SerializedName("weathercode")
        private int weatherCode;

        public double getTemperature() {
            return temperature;
        }

        public int getWeatherCode() {
            return weatherCode;
        }
    }
}
