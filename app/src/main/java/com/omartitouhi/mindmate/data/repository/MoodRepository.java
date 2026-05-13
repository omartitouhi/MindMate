package com.omartitouhi.mindmate.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omartitouhi.mindmate.data.local.AppDatabase;
import com.omartitouhi.mindmate.data.local.MoodDao;
import com.omartitouhi.mindmate.data.local.MoodEntity;
import com.omartitouhi.mindmate.data.model.Mood;
import com.omartitouhi.mindmate.data.model.WeatherInfo;
import com.omartitouhi.mindmate.utils.Resource;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MoodRepository {
    public interface MoodCallback {
        void onResult(Resource<Mood> resource);
    }

    private final MoodDao moodDao;
    private final FirebaseFirestore firestore;
    private final FirebaseAuth firebaseAuth;
    private final WeatherRepository weatherRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public MoodRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        moodDao = database.moodDao();
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        weatherRepository = new WeatherRepository(context);
    }

    public LiveData<List<MoodEntity>> getLocalMoods() {
        return moodDao.getAllMoods();
    }

    public void saveMood(String moodValue, int stressScore, String note, MoodCallback callback) {
        callback.onResult(Resource.loading());

        String userId = firebaseAuth.getCurrentUser() != null
                ? firebaseAuth.getCurrentUser().getUid()
                : "anonymous";
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
                saveMoodInFirestore(mood, callback);
            } catch (Exception exception) {
                callback.onResult(Resource.error(getReadableError(exception)));
            }
        });
    }

    private void saveMoodInFirestore(Mood mood, MoodCallback callback) {
        firestore.collection("users")
                .document(mood.getUserId())
                .collection("moods")
                .document(mood.getId())
                .set(mood)
                .addOnSuccessListener(unused -> executorService.execute(() -> {
                    moodDao.markAsSynced(mood.getId());
                    callback.onResult(Resource.success(mood));
                }))
                .addOnFailureListener(exception -> callback.onResult(Resource.error(getReadableError(exception))));
    }

    private String getReadableError(Exception exception) {
        String message = exception.getLocalizedMessage();
        if (message == null || message.trim().isEmpty()) {
            return "Impossible de sauvegarder l'humeur pour le moment.";
        }
        return message;
    }
}
