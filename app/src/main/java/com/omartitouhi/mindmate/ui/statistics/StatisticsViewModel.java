package com.omartitouhi.mindmate.ui.statistics;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.omartitouhi.mindmate.data.model.StatisticsSummary;
import com.omartitouhi.mindmate.data.repository.StatisticsRepository;

public class StatisticsViewModel extends AndroidViewModel {
    private final StatisticsRepository statisticsRepository;

    public StatisticsViewModel(@NonNull Application application) {
        super(application);
        statisticsRepository = new StatisticsRepository(application);
    }

    public LiveData<StatisticsSummary> getSummary() {
        return statisticsRepository.getSummary();
    }
}
