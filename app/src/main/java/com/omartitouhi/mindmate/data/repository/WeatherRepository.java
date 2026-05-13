package com.omartitouhi.mindmate.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

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

    public WeatherRepository(Context context) {
        weatherApiService = ApiClient.getWeatherApiService();
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void getCurrentWeather(WeatherCallback callback) {
        callback.onResult(Resource.loading());
        weatherApiService.getCurrentWeather(DEFAULT_LATITUDE, DEFAULT_LONGITUDE, true, "auto")
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                        WeatherResponse body = response.body();
                        if (!response.isSuccessful() || body == null || body.getCurrentWeather() == null) {
                            callback.onResult(Resource.error("Impossible de recuperer la meteo actuelle."));
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
            return "Clear";
        }
        if (code == 1 || code == 2 || code == 3) {
            return "Cloudy";
        }
        if ((code >= 45 && code <= 48)) {
            return "Fog";
        }
        if ((code >= 51 && code <= 67) || (code >= 80 && code <= 82)) {
            return "Rain";
        }
        if (code >= 71 && code <= 77) {
            return "Snow";
        }
        if (code >= 95) {
            return "Storm";
        }
        return "Mild";
    }

    private String buildAdvice(String condition) {
        if ("Clear".equals(condition)) {
            return "Profitez de la lumiere naturelle pour une courte marche mindful.";
        }
        if ("Cloudy".equals(condition) || "Fog".equals(condition)) {
            return "Gardez un rythme doux et notez ce qui vous aide a rester ancre.";
        }
        if ("Rain".equals(condition) || "Storm".equals(condition)) {
            return "Essayez une respiration calme ou une entree journal au chaud.";
        }
        if ("Snow".equals(condition)) {
            return "Prenez soin de votre energie et planifiez une pause reparatrice.";
        }
        return "Ecoutez votre corps et choisissez un check-in simple aujourd'hui.";
    }

    private String getReadableError(Throwable throwable) {
        String message = throwable.getLocalizedMessage();
        if (message == null || message.trim().isEmpty()) {
            return "La meteo est indisponible pour le moment.";
        }
        return message;
    }
}
