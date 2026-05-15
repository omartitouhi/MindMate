package com.omartitouhi.mindmate.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.omartitouhi.mindmate.data.repository.SettingsRepository;
import com.omartitouhi.mindmate.utils.Resource;

public class SettingsViewModel extends AndroidViewModel {
    public static final String PREFS_NAME = SettingsRepository.PREFS_NAME;
    public static final String KEY_NOTIFICATIONS_ENABLED = SettingsRepository.KEY_NOTIFICATIONS_ENABLED;
    public static final String KEY_JOURNAL_REMINDER_HOUR = SettingsRepository.KEY_JOURNAL_REMINDER_HOUR;
    public static final String KEY_JOURNAL_REMINDER_MINUTE = SettingsRepository.KEY_JOURNAL_REMINDER_MINUTE;
    public static final String KEY_DARK_MODE = SettingsRepository.KEY_DARK_MODE;

    private final SettingsRepository settingsRepository;
    private final MutableLiveData<Resource<String>> settingsState = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        settingsRepository = new SettingsRepository(application);
    }

    public LiveData<Resource<String>> getSettingsState() {
        return settingsState;
    }

    public boolean areNotificationsEnabled() {
        return settingsRepository.areNotificationsEnabled();
    }

    public int getReminderHour() {
        return settingsRepository.getReminderHour();
    }

    public int getReminderMinute() {
        return settingsRepository.getReminderMinute();
    }

    public boolean isDarkModeEnabled() {
        return settingsRepository.isDarkModeEnabled();
    }

    public void setNotificationsEnabled(boolean enabled) {
        settingsRepository.setNotificationsEnabled(enabled);
    }

    public void setReminderTime(int hour, int minute) {
        settingsRepository.setReminderTime(hour, minute);
    }

    public void setDarkModeEnabled(boolean enabled) {
        settingsRepository.setDarkModeEnabled(enabled);
    }

    public void deleteLocalData() {
        settingsRepository.deleteLocalData(settingsState::postValue);
    }
}
