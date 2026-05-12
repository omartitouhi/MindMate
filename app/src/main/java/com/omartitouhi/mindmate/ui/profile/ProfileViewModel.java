package com.omartitouhi.mindmate.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> title = new MutableLiveData<>("Profil");

    public LiveData<String> getTitle() {
        return title;
    }
}
