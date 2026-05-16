package com.omartitouhi.mindmate.data.remote;

import com.omartitouhi.mindmate.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    private static final String BASE_URL = BuildConfig.MINDMATE_API_BASE_URL;
    private static final String WEATHER_BASE_URL = BuildConfig.WEATHER_API_BASE_URL;
    private static final String N8N_BASE_URL = "https://n8n-mkr-test.duckdns.org/";
    private static MindMateApiService apiService;
    private static WeatherApiService weatherApiService;
    private static AiApiService aiApiService;
    private static N8nChatApiService n8nChatApiService;

    private ApiClient() {
    }

    public static MindMateApiService getApiService() {
        if (apiService == null) {
            apiService = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(createClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MindMateApiService.class);
        }
        return apiService;
    }

    public static WeatherApiService getWeatherApiService() {
        if (weatherApiService == null) {
            weatherApiService = new Retrofit.Builder()
                    .baseUrl(WEATHER_BASE_URL)
                    .client(createClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(WeatherApiService.class);
        }
        return weatherApiService;
    }

    public static AiApiService getAiApiService() {
        if (aiApiService == null) {
            aiApiService = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(createClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(AiApiService.class);
        }
        return aiApiService;
    }

    public static N8nChatApiService getN8nChatApiService() {
        if (n8nChatApiService == null) {
            n8nChatApiService = new Retrofit.Builder()
                    .baseUrl(N8N_BASE_URL)
                    .client(createClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(N8nChatApiService.class);
        }
        return n8nChatApiService;
    }

    private static OkHttpClient createClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(loggingInterceptor);
        }
        return builder.build();
    }
}
