package com.omartitouhi.mindmate.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.data.model.WeatherInfo;
import com.omartitouhi.mindmate.databinding.FragmentHomeBinding;
import com.omartitouhi.mindmate.utils.Resource;

import java.util.Locale;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private WeatherViewModel weatherViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        binding.refreshWeatherButton.setOnClickListener(v -> weatherViewModel.loadCurrentWeather());
        weatherViewModel.getWeatherState().observe(getViewLifecycleOwner(), this::renderWeatherState);
        weatherViewModel.loadCurrentWeather();
    }

    private void renderWeatherState(Resource<WeatherInfo> state) {
        if (state == null) {
            return;
        }

        boolean loading = state.getStatus() == Resource.Status.LOADING;
        binding.weatherLoadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.refreshWeatherButton.setEnabled(!loading);
        binding.weatherMessageText.setVisibility(state.getStatus() == Resource.Status.ERROR ? View.VISIBLE : View.GONE);
        binding.weatherMessageText.setText(state.getMessage());

        if (state.getStatus() == Resource.Status.SUCCESS && state.getData() != null) {
            WeatherInfo weatherInfo = state.getData();
            binding.cityText.setText(weatherInfo.getCity());
            binding.temperatureText.setText(getString(R.string.weather_temperature, weatherInfo.getTemperature()));
            binding.conditionText.setText(weatherInfo.getCondition());
            binding.weatherAdviceText.setText(weatherInfo.getAdvice());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
