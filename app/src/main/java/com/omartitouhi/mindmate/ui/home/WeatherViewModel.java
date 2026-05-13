package com.omartitouhi.mindmate.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.omartitouhi.mindmate.data.model.WeatherInfo;
import com.omartitouhi.mindmate.data.repository.WeatherRepository;
import com.omartitouhi.mindmate.utils.Resource;

public class WeatherViewModel extends AndroidViewModel {
    private final WeatherRepository weatherRepository;
    private final MutableLiveData<Resource<WeatherInfo>> weatherState = new MutableLiveData<>();

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        weatherRepository = new WeatherRepository(application);
    }

    public LiveData<Resource<WeatherInfo>> getWeatherState() {
        return weatherState;
    }

    public void loadCurrentWeather() {
        weatherRepository.getCurrentWeather(weatherState::postValue);
    }
}
