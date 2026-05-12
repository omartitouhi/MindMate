package com.omartitouhi.mindmate.ui.ai;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AiViewModel extends ViewModel {
    private final MutableLiveData<String> title = new MutableLiveData<>("Assistant IA");

    public LiveData<String> getTitle() {
        return title;
    }
}
