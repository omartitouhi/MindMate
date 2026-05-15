package com.omartitouhi.mindmate.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.omartitouhi.mindmate.R;

public class JournalReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new NotificationHelper(context).showNotification(
                NotificationHelper.TYPE_DAILY_JOURNAL,
                context.getString(R.string.notification_daily_journal_title),
                context.getString(R.string.notification_daily_journal_body)
        );
    }
}
