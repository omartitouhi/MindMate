package com.omartitouhi.mindmate.ui.settings;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.omartitouhi.mindmate.data.local.AppDatabase;
import com.omartitouhi.mindmate.utils.NotificationHelper;
import com.omartitouhi.mindmate.utils.Resource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsViewModel extends AndroidViewModel {
    public static final String PREFS_NAME = "mindmate_settings";
    public static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    public static final String KEY_JOURNAL_REMINDER_HOUR = "journal_reminder_hour";
    public static final String KEY_JOURNAL_REMINDER_MINUTE = "journal_reminder_minute";
    public static final String KEY_DARK_MODE = "dark_mode";

    private final SharedPreferences sharedPreferences;
    private final NotificationHelper notificationHelper;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MutableLiveData<Resource<String>> settingsState = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        notificationHelper = new NotificationHelper(application);
    }

    public LiveData<Resource<String>> getSettingsState() {
        return settingsState;
    }

    public boolean areNotificationsEnabled() {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    public int getReminderHour() {
        return sharedPreferences.getInt(KEY_JOURNAL_REMINDER_HOUR, 20);
    }

    public int getReminderMinute() {
        return sharedPreferences.getInt(KEY_JOURNAL_REMINDER_MINUTE, 0);
    }

    public boolean isDarkModeEnabled() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }

    public void setNotificationsEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
        if (enabled) {
            notificationHelper.scheduleDailyJournalReminder(getReminderHour(), getReminderMinute());
        } else {
            notificationHelper.cancelDailyJournalReminder();
        }
    }

    public void setReminderTime(int hour, int minute) {
        sharedPreferences.edit()
                .putInt(KEY_JOURNAL_REMINDER_HOUR, hour)
                .putInt(KEY_JOURNAL_REMINDER_MINUTE, minute)
                .apply();
        if (areNotificationsEnabled()) {
            notificationHelper.scheduleDailyJournalReminder(hour, minute);
        }
    }

    public void setDarkModeEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
        AppCompatDelegate.setDefaultNightMode(enabled
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }

    public void deleteLocalData() {
        settingsState.setValue(Resource.loading());
        executorService.execute(() -> {
            try {
                AppDatabase.getInstance(getApplication()).clearAllTables();
                settingsState.postValue(Resource.success("Donnees locales supprimees."));
            } catch (Exception exception) {
                String message = exception.getLocalizedMessage();
                settingsState.postValue(Resource.error(message == null ? "Impossible de supprimer les donnees locales." : message));
            }
        });
    }
}
