package com.omartitouhi.mindmate.ui.mood;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MoodViewModel extends ViewModel {
    private final MutableLiveData<String> title = new MutableLiveData<>("Suivi de l'humeur");

    public LiveData<String> getTitle() {
        return title;
    }
}
