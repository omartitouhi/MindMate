package com.omartitouhi.mindmate.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.data.repository.SettingsRepository;

public class JournalReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent == null ? null : intent.getAction())) {
            SharedPreferences preferences = context.getSharedPreferences(SettingsRepository.PREFS_NAME, Context.MODE_PRIVATE);
            if (preferences.getBoolean(SettingsRepository.KEY_NOTIFICATIONS_ENABLED, true)) {
                int hour = preferences.getInt(SettingsRepository.KEY_JOURNAL_REMINDER_HOUR, 20);
                int minute = preferences.getInt(SettingsRepository.KEY_JOURNAL_REMINDER_MINUTE, 0);
                notificationHelper.scheduleDailyJournalReminder(hour, minute);
            }
            return;
        }

        notificationHelper.showNotification(
                NotificationHelper.TYPE_DAILY_JOURNAL,
                context.getString(R.string.notification_daily_journal_title),
                context.getString(R.string.notification_daily_journal_body)
        );
    }
}
