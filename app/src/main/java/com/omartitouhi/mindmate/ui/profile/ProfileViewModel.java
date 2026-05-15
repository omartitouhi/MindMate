package com.omartitouhi.mindmate.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.omartitouhi.mindmate.data.model.StatisticsSummary;
import com.omartitouhi.mindmate.data.model.User;
import com.omartitouhi.mindmate.data.repository.ProfileRepository;
import com.omartitouhi.mindmate.data.repository.StatisticsRepository;
import com.omartitouhi.mindmate.utils.Resource;

public class ProfileViewModel extends AndroidViewModel {
    private final ProfileRepository profileRepository = new ProfileRepository();
    private final StatisticsRepository statisticsRepository;
    private final MutableLiveData<Resource<String>> profileState = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        statisticsRepository = new StatisticsRepository(application);
        loadUserProfile();
    }

    public LiveData<User> getUserProfile() {
        return profileRepository.getUserProfile();
    }

    public LiveData<Resource<String>> getProfileState() {
        return profileState;
    }

    public LiveData<StatisticsSummary> getStatisticsSummary() {
        return statisticsRepository.getSummary();
    }

    public void loadUserProfile() {
        profileRepository.loadUserProfile();
    }

    public void updateDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            profileState.setValue(Resource.error("Veuillez saisir un nom."));
            return;
        }
        profileRepository.updateDisplayName(displayName, profileState::setValue);
    }

    public void logout() {
        profileRepository.logout();
    }
}
