package com.omartitouhi.mindmate.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> title = new MutableLiveData<>("Accueil");

    public LiveData<String> getTitle() {
        return title;
    }
}
