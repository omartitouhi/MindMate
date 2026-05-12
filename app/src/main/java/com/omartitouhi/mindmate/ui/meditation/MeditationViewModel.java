package com.omartitouhi.mindmate.ui.meditation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MeditationViewModel extends ViewModel {
    private final MutableLiveData<String> title = new MutableLiveData<>("Respiration et meditation");

    public LiveData<String> getTitle() {
        return title;
    }
}
