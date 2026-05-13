package com.omartitouhi.mindmate.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omartitouhi.mindmate.data.local.AppDatabase;
import com.omartitouhi.mindmate.data.local.JournalDao;
import com.omartitouhi.mindmate.data.local.JournalEntity;
import com.omartitouhi.mindmate.data.model.JournalEntry;
import com.omartitouhi.mindmate.utils.Resource;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JournalRepository {
    public interface JournalCallback {
        void onResult(Resource<JournalEntry> resource);
    }

    private final JournalDao journalDao;
    private final FirebaseFirestore firestore;
    private final FirebaseAuth firebaseAuth;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public JournalRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        journalDao = database.journalDao();
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public LiveData<List<JournalEntity>> getJournalEntries() {
        return journalDao.getAllJournalEntries();
    }

    public LiveData<JournalEntity> getJournalEntry(String id) {
        return journalDao.getJournalEntry(id);
    }

    public void addEntry(String title, String content, String mood, JournalCallback callback) {
        long now = System.currentTimeMillis();
        JournalEntry entry = new JournalEntry(
                UUID.randomUUID().toString(),
                getUserId(),
                title,
                content,
                mood,
                now,
                now
        );
        saveEntry(entry, callback);
    }

    public void updateEntry(String id, long createdAt, String title, String content, String mood, JournalCallback callback) {
        JournalEntry entry = new JournalEntry(
                id,
                getUserId(),
                title,
                content,
                mood,
                createdAt,
                System.currentTimeMillis()
        );
        saveEntry(entry, callback);
    }

    public void deleteEntry(JournalEntity entity, JournalCallback callback) {
        callback.onResult(Resource.loading());
        executorService.execute(() -> {
            try {
                journalDao.delete(entity);
                firestore.collection("users")
                        .document(entity.getUserId())
                        .collection("journal_entries")
                        .document(entity.getId())
                        .delete()
                        .addOnSuccessListener(unused -> callback.onResult(Resource.success(entity.toJournalEntry())))
                        .addOnFailureListener(exception -> callback.onResult(Resource.error(getReadableError(exception))));
            } catch (Exception exception) {
                callback.onResult(Resource.error(getReadableError(exception)));
            }
        });
    }

    private void saveEntry(JournalEntry entry, JournalCallback callback) {
        callback.onResult(Resource.loading());
        executorService.execute(() -> {
            try {
                journalDao.insert(JournalEntity.fromJournalEntry(entry, false));
                firestore.collection("users")
                        .document(entry.getUserId())
                        .collection("journal_entries")
                        .document(entry.getId())
                        .set(entry)
                        .addOnSuccessListener(unused -> executorService.execute(() -> {
                            journalDao.markAsSynced(entry.getId());
                            callback.onResult(Resource.success(entry));
                        }))
                        .addOnFailureListener(exception -> callback.onResult(Resource.error(getReadableError(exception))));
            } catch (Exception exception) {
                callback.onResult(Resource.error(getReadableError(exception)));
            }
        });
    }

    private String getUserId() {
        return firebaseAuth.getCurrentUser() != null
                ? firebaseAuth.getCurrentUser().getUid()
                : "anonymous";
    }

    private String getReadableError(Exception exception) {
        String message = exception.getLocalizedMessage();
        if (message == null || message.trim().isEmpty()) {
            return "Impossible de synchroniser le journal pour le moment.";
        }
        return message;
    }
}
