package com.omartitouhi.mindmate.ui.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StatisticsViewModel extends ViewModel {
    private final MutableLiveData<String> title = new MutableLiveData<>("Statistiques");

    public LiveData<String> getTitle() {
        return title;
    }
}
