package com.omartitouhi.mindmate.utils;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.SplashActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NotificationHelper {
    public static final String CHANNEL_REMINDERS = "mindmate_reminders";
    public static final String CHANNEL_MESSAGES = "mindmate_messages";

    public static final String TYPE_DAILY_JOURNAL = "daily_journal";
    public static final String TYPE_MEDITATION = "meditation";
    public static final String TYPE_MOTIVATION = "motivation";

    private static final int JOURNAL_REMINDER_REQUEST_CODE = 1001;

    private final Context context;

    public NotificationHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    public void createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager == null) {
            return;
        }

        NotificationChannel remindersChannel = new NotificationChannel(
                CHANNEL_REMINDERS,
                context.getString(R.string.notification_channel_reminders),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        remindersChannel.setDescription(context.getString(R.string.notification_channel_reminders_description));

        NotificationChannel messagesChannel = new NotificationChannel(
                CHANNEL_MESSAGES,
                context.getString(R.string.notification_channel_messages),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        messagesChannel.setDescription(context.getString(R.string.notification_channel_messages_description));

        notificationManager.createNotificationChannel(remindersChannel);
        notificationManager.createNotificationChannel(messagesChannel);
    }

    public void showNotification(String type, String title, String body) {
        createNotificationChannels();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String channelId = TYPE_MOTIVATION.equals(type) ? CHANNEL_MESSAGES : CHANNEL_REMINDERS;
        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                type.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat.from(context).notify((int) System.currentTimeMillis(), builder.build());
    }

    public void scheduleDailyJournalReminder() {
        Intent intent = new Intent(context, JournalReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                JOURNAL_REMINDER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }

    public static void fetchAndSaveFcmToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(NotificationHelper::saveFcmToken);
    }

    public static void saveFcmToken(String token) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        if (userId == null || token == null || token.trim().isEmpty()) {
            return;
        }

        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("token", token);
        tokenData.put("updatedAt", System.currentTimeMillis());

        FirebaseFirestore.getInstance()
                .collection(Constants.FIRESTORE_USERS)
                .document(userId)
                .collection("fcm_tokens")
                .document(token)
                .set(tokenData);
    }
}
