package com.omartitouhi.mindmate.ui.journal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class JournalViewModel extends ViewModel {
    private final MutableLiveData<String> title = new MutableLiveData<>("Journal personnel");

    public LiveData<String> getTitle() {
        return title;
    }
}
