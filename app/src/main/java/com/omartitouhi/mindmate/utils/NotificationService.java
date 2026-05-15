package com.omartitouhi.mindmate.utils;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.omartitouhi.mindmate.R;

import java.util.Map;

public class NotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Map<String, String> data = message.getData();
        String type = data.containsKey("type") ? data.get("type") : NotificationHelper.TYPE_MOTIVATION;
        String title = data.containsKey("title") ? data.get("title") : getDefaultTitle(type);
        String body = data.containsKey("body") ? data.get("body") : getDefaultBody(type);

        if (message.getNotification() != null) {
            if (message.getNotification().getTitle() != null) {
                title = message.getNotification().getTitle();
            }
            if (message.getNotification().getBody() != null) {
                body = message.getNotification().getBody();
            }
        }

        new NotificationHelper(this).showNotification(type, title, body);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        NotificationHelper.saveFcmToken(token);
    }

    private String getDefaultTitle(String type) {
        if (NotificationHelper.TYPE_DAILY_JOURNAL.equals(type)) {
            return getString(R.string.notification_daily_journal_title);
        }
        if (NotificationHelper.TYPE_MEDITATION.equals(type)) {
            return getString(R.string.notification_meditation_title);
        }
        return getString(R.string.notification_motivation_title);
    }

    private String getDefaultBody(String type) {
        if (NotificationHelper.TYPE_DAILY_JOURNAL.equals(type)) {
            return getString(R.string.notification_daily_journal_body);
        }
        if (NotificationHelper.TYPE_MEDITATION.equals(type)) {
            return getString(R.string.notification_meditation_body);
        }
        return getString(R.string.notification_motivation_body);
    }
}
