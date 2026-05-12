package com.omartitouhi.mindmate.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthViewModel extends ViewModel {
    private final MutableLiveData<String> title = new MutableLiveData<>("Connexion");

    public LiveData<String> getTitle() {
        return title;
    }
}
