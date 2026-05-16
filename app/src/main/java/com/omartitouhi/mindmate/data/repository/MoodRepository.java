package com.omartitouhi.mindmate.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omartitouhi.mindmate.data.local.AppDatabase;
import com.omartitouhi.mindmate.data.local.MoodDao;
import com.omartitouhi.mindmate.data.local.MoodEntity;
import com.omartitouhi.mindmate.data.model.Mood;
import com.omartitouhi.mindmate.data.model.WeatherInfo;
import com.omartitouhi.mindmate.utils.NetworkUtils;
import com.omartitouhi.mindmate.utils.Resource;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MoodRepository {
    private static final String TAG = "MoodRepository";

    public interface MoodCallback {
        void onResult(Resource<Mood> resource);
    }

    public interface SyncCallback {
        void onResult(Resource<String> resource);
    }

    private final Context appContext;
    private final MoodDao moodDao;
    private final FirebaseFirestore firestore;
    private final FirebaseAuth firebaseAuth;
    private final WeatherRepository weatherRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final NetworkUtils.NetworkMonitor networkMonitor;

    public MoodRepository(Context context) {
        appContext = context.getApplicationContext();
        AppDatabase database = AppDatabase.getInstance(appContext);
        moodDao = database.moodDao();
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        weatherRepository = new WeatherRepository(appContext);
        networkMonitor = NetworkUtils.registerNetworkMonitor(appContext, () -> syncPendingMoods(null));
    }

    public LiveData<List<MoodEntity>> getLocalMoods() {
        return moodDao.getMoodsForUser(getUserId());
    }

    public void saveMood(String moodValue, int stressScore, String note, MoodCallback callback) {
        callback.onResult(Resource.loading());

        String userId = getUserId();
        WeatherInfo latestWeather = weatherRepository.getLatestWeather();
        Mood mood = new Mood(
                UUID.randomUUID().toString(),
                userId,
                moodValue,
                stressScore,
                note,
                System.currentTimeMillis(),
                latestWeather == null ? null : latestWeather.getCity(),
                latestWeather == null ? null : latestWeather.getTemperature(),
                latestWeather == null ? null : latestWeather.getCondition()
        );

        executorService.execute(() -> {
            try {
                moodDao.insert(MoodEntity.fromMood(mood, false));
                Log.d(TAG, "Mood saved locally: " + mood.getId());
                callback.onResult(Resource.success(mood));
                if (NetworkUtils.isNetworkAvailable(appContext)) {
                    syncMoodInFirestore(mood);
                    syncPendingMoods(null);
                } else {
                    Log.d(TAG, "Offline mode, mood pending sync: " + mood.getId());
                }
            } catch (Exception exception) {
                callback.onResult(Resource.error(getReadableError(exception)));
            }
        });
    }

    public void retrySync(SyncCallback callback) {
        syncPendingMoods(callback);
    }

    private void syncPendingMoods(SyncCallback callback) {
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
            List<MoodEntity> pendingMoods = moodDao.getUnsyncedMoodsForUser(getUserId());
            if (pendingMoods.isEmpty()) {
                if (callback != null) {
                    callback.onResult(Resource.success("Toutes les humeurs sont synchronisees."));
                }
                return;
            }
            for (MoodEntity entity : pendingMoods) {
                syncMoodInFirestore(entity.toMood());
            }
            if (callback != null) {
                callback.onResult(Resource.success("Synchronisation des humeurs lancee."));
            }
        });
    }

    private void syncMoodInFirestore(Mood mood) {
        firestore.collection("users")
                .document(mood.getUserId())
                .collection("moods")
                .document(mood.getId())
                .set(mood)
                .addOnSuccessListener(unused -> executorService.execute(() -> {
                    moodDao.markAsSynced(mood.getId());
                    Log.d(TAG, "Mood synced: " + mood.getId());
                }))
                .addOnFailureListener(exception -> Log.e(TAG, "Mood sync failed: " + mood.getId(), exception));
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
            return "Impossible de sauvegarder l'humeur pour le moment.";
        }
        return message;
    }
}
