package com.omartitouhi.mindmate.data.remote;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    private static final String BASE_URL = "https://example.com/api/";
    private static final String WEATHER_BASE_URL = "https://api.open-meteo.com/v1/";
    private static MindMateApiService apiService;
    private static WeatherApiService weatherApiService;

    private ApiClient() {
    }

    public static MindMateApiService getApiService() {
        if (apiService == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            apiService = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MindMateApiService.class);
        }
        return apiService;
    }

    public static WeatherApiService getWeatherApiService() {
        if (weatherApiService == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            weatherApiService = new Retrofit.Builder()
                    .baseUrl(WEATHER_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(WeatherApiService.class);
        }
        return weatherApiService;
    }
}
