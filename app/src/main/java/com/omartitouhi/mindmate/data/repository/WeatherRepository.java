package com.omartitouhi.mindmate.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.data.model.WeatherInfo;
import com.omartitouhi.mindmate.data.remote.ApiClient;
import com.omartitouhi.mindmate.data.remote.WeatherApiService;
import com.omartitouhi.mindmate.data.remote.WeatherResponse;
import com.omartitouhi.mindmate.utils.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {
    public interface WeatherCallback {
        void onResult(Resource<WeatherInfo> resource);
    }

    private static final String PREFS_NAME = "weather_cache";
    private static final String KEY_CITY = "city";
    private static final String KEY_TEMPERATURE = "temperature";
    private static final String KEY_CONDITION = "condition";

    private static final String DEFAULT_CITY = "Tunis";
    private static final double DEFAULT_LATITUDE = 36.8065;
    private static final double DEFAULT_LONGITUDE = 10.1815;

    private final WeatherApiService weatherApiService;
    private final SharedPreferences sharedPreferences;
    private final Context appContext;

    public WeatherRepository(Context context) {
        appContext = context.getApplicationContext();
        weatherApiService = ApiClient.getWeatherApiService();
        sharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void getCurrentWeather(WeatherCallback callback) {
        callback.onResult(Resource.loading());
        weatherApiService.getCurrentWeather(DEFAULT_LATITUDE, DEFAULT_LONGITUDE, true, "auto")
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                        WeatherResponse body = response.body();
                        if (!response.isSuccessful() || body == null || body.getCurrentWeather() == null) {
                            callback.onResult(Resource.error(appContext.getString(R.string.weather_error_current)));
                            return;
                        }

                        WeatherInfo weatherInfo = toWeatherInfo(body.getCurrentWeather());
                        saveLatestWeather(weatherInfo);
                        callback.onResult(Resource.success(weatherInfo));
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable throwable) {
                        WeatherInfo cachedWeather = getLatestWeather();
                        if (cachedWeather != null) {
                            callback.onResult(Resource.success(cachedWeather));
                            return;
                        }
                        callback.onResult(Resource.error(getReadableError(throwable)));
                    }
                });
    }

    public WeatherInfo getLatestWeather() {
        if (!sharedPreferences.contains(KEY_CONDITION)) {
            return null;
        }
        return new WeatherInfo(
                sharedPreferences.getString(KEY_CITY, DEFAULT_CITY),
                Double.longBitsToDouble(sharedPreferences.getLong(KEY_TEMPERATURE, Double.doubleToLongBits(0))),
                sharedPreferences.getString(KEY_CONDITION, ""),
                buildAdvice(sharedPreferences.getString(KEY_CONDITION, ""))
        );
    }

    private void saveLatestWeather(WeatherInfo weatherInfo) {
        sharedPreferences.edit()
                .putString(KEY_CITY, weatherInfo.getCity())
                .putLong(KEY_TEMPERATURE, Double.doubleToLongBits(weatherInfo.getTemperature()))
                .putString(KEY_CONDITION, weatherInfo.getCondition())
                .apply();
    }

    private WeatherInfo toWeatherInfo(WeatherResponse.CurrentWeather currentWeather) {
        String condition = mapWeatherCode(currentWeather.getWeatherCode());
        return new WeatherInfo(
                DEFAULT_CITY,
                currentWeather.getTemperature(),
                condition,
                buildAdvice(condition)
        );
    }

    private String mapWeatherCode(int code) {
        if (code == 0) {
            return appContext.getString(R.string.weather_condition_clear);
        }
        if (code == 1 || code == 2 || code == 3) {
            return appContext.getString(R.string.weather_condition_cloudy);
        }
        if ((code >= 45 && code <= 48)) {
            return appContext.getString(R.string.weather_condition_fog);
        }
        if ((code >= 51 && code <= 67) || (code >= 80 && code <= 82)) {
            return appContext.getString(R.string.weather_condition_rain);
        }
        if (code >= 71 && code <= 77) {
            return appContext.getString(R.string.weather_condition_snow);
        }
        if (code >= 95) {
            return appContext.getString(R.string.weather_condition_storm);
        }
        return appContext.getString(R.string.weather_condition_mild);
    }

    private String buildAdvice(String condition) {
        if (appContext.getString(R.string.weather_condition_clear).equals(condition)) {
            return appContext.getString(R.string.weather_advice_clear);
        }
        if (appContext.getString(R.string.weather_condition_cloudy).equals(condition)
                || appContext.getString(R.string.weather_condition_fog).equals(condition)) {
            return appContext.getString(R.string.weather_advice_cloudy);
        }
        if (appContext.getString(R.string.weather_condition_rain).equals(condition)
                || appContext.getString(R.string.weather_condition_storm).equals(condition)) {
            return appContext.getString(R.string.weather_advice_rain);
        }
        if (appContext.getString(R.string.weather_condition_snow).equals(condition)) {
            return appContext.getString(R.string.weather_advice_snow);
        }
        return appContext.getString(R.string.weather_advice_default);
    }

    private String getReadableError(Throwable throwable) {
        String message = throwable.getLocalizedMessage();
        if (message == null || message.trim().isEmpty()) {
            return appContext.getString(R.string.weather_error_unavailable);
        }
        return message;
    }
}
