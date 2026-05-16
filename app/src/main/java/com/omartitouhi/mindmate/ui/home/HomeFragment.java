package com.omartitouhi.mindmate.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

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
        binding.shortcutJournalButton.setOnClickListener(v -> navigateSingleTop(R.id.journalListFragment));
        binding.shortcutMoodButton.setOnClickListener(v -> navigateSingleTop(R.id.moodCheckInFragment));
        binding.shortcutMeditationButton.setOnClickListener(v -> navigateSingleTop(R.id.meditationFragment));
        binding.shortcutChatButton.setOnClickListener(v -> navigateSingleTop(R.id.aiChatFragment));
        binding.shortcutStatisticsButton.setOnClickListener(v -> navigateSingleTop(R.id.statisticsFragment));
        weatherViewModel.getWeatherState().observe(getViewLifecycleOwner(), this::renderWeatherState);
        weatherViewModel.loadCurrentWeather();
    }

    private void navigateSingleTop(int destinationId) {
        NavController navController = NavHostFragment.findNavController(this);
        if (navController.getCurrentDestination() != null
                && navController.getCurrentDestination().getId() == destinationId) {
            return;
        }
        NavOptions navOptions = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.homeFragment, false)
                .build();
        navController.navigate(destinationId, null, navOptions);
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
