package com.omartitouhi.mindmate.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omartitouhi.mindmate.data.local.AppDatabase;
import com.omartitouhi.mindmate.data.local.JournalDao;
import com.omartitouhi.mindmate.data.local.JournalEntity;
import com.omartitouhi.mindmate.data.model.JournalEntry;
import com.omartitouhi.mindmate.utils.NetworkUtils;
import com.omartitouhi.mindmate.utils.Resource;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JournalRepository {
    private static final String TAG = "JournalRepository";

    public interface JournalCallback {
        void onResult(Resource<JournalEntry> resource);
    }

    public interface SyncCallback {
        void onResult(Resource<String> resource);
    }

    private final Context appContext;
    private final JournalDao journalDao;
    private final FirebaseFirestore firestore;
    private final FirebaseAuth firebaseAuth;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final NetworkUtils.NetworkMonitor networkMonitor;

    public JournalRepository(Context context) {
        appContext = context.getApplicationContext();
        AppDatabase database = AppDatabase.getInstance(appContext);
        journalDao = database.journalDao();
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        networkMonitor = NetworkUtils.registerNetworkMonitor(appContext, () -> syncPendingEntries(null));
    }

    public LiveData<List<JournalEntity>> getJournalEntries() {
        return journalDao.getJournalEntriesForUser(getUserId());
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
                journalDao.markPendingDelete(entity.getId(), System.currentTimeMillis());
                Log.d(TAG, "Journal marked pending delete locally: " + entity.getId());
                callback.onResult(Resource.success(entity.toJournalEntry()));
                if (NetworkUtils.isNetworkAvailable(appContext)) {
                    syncPendingEntries(null);
                }
            } catch (Exception exception) {
                callback.onResult(Resource.error(getReadableError(exception)));
            }
        });
    }

    public void retrySync(SyncCallback callback) {
        syncPendingEntries(callback);
    }

    private void saveEntry(JournalEntry entry, JournalCallback callback) {
        callback.onResult(Resource.loading());
        executorService.execute(() -> {
            try {
                journalDao.insert(JournalEntity.fromJournalEntry(entry, false));
                Log.d(TAG, "Journal saved locally: " + entry.getId());
                callback.onResult(Resource.success(entry));
                if (NetworkUtils.isNetworkAvailable(appContext)) {
                    syncEntryInFirestore(entry);
                    syncPendingEntries(null);
                } else {
                    Log.d(TAG, "Offline mode, journal pending sync: " + entry.getId());
                }
            } catch (Exception exception) {
                callback.onResult(Resource.error(getReadableError(exception)));
            }
        });
    }

    private void syncPendingEntries(SyncCallback callback) {
        if (!NetworkUtils.isNetworkAvailable(appContext)) {
            if (callback != null) {
                callback.onResult(Resource.error("Pas de connexion. Synchronisation en attente."));
            }
            return;
        }
        if (callback != null) {
            callback.onResult(Resource.loading());
        }
        executorService.execute(() -> {
            List<JournalEntity> pendingEntries = journalDao.getUnsyncedJournalEntriesForUser(getUserId());
            if (pendingEntries.isEmpty() && callback != null) {
                callback.onResult(Resource.success("Toutes les entrees journal sont synchronisees."));
            }
            for (JournalEntity entity : pendingEntries) {
                if (entity.isPendingDelete()) {
                    deleteEntryInFirestore(entity);
                } else {
                    syncEntryInFirestore(entity.toJournalEntry());
                }
            }
            if (!pendingEntries.isEmpty() && callback != null) {
                callback.onResult(Resource.success("Synchronisation du journal lancee."));
            }
        });
    }

    private void syncEntryInFirestore(JournalEntry entry) {
        firestore.collection("users")
                .document(entry.getUserId())
                .collection("journal_entries")
                .document(entry.getId())
                .set(entry)
                .addOnSuccessListener(unused -> executorService.execute(() -> {
                    journalDao.markAsSynced(entry.getId());
                    Log.d(TAG, "Journal synced: " + entry.getId());
                }))
                .addOnFailureListener(exception -> Log.e(TAG, "Journal sync failed: " + entry.getId(), exception));
    }

    private void deleteEntryInFirestore(JournalEntity entity) {
        firestore.collection("users")
                .document(entity.getUserId())
                .collection("journal_entries")
                .document(entity.getId())
                .delete()
                .addOnSuccessListener(unused -> executorService.execute(() -> {
                    journalDao.deleteById(entity.getId());
                    Log.d(TAG, "Journal deleted remotely and locally: " + entity.getId());
                }))
                .addOnFailureListener(exception -> Log.e(TAG, "Pending delete failed: " + entity.getId(), exception));
    }

    public void dispose() {
        networkMonitor.unregister();
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
