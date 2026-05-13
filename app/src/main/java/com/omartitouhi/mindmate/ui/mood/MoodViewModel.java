package com.omartitouhi.mindmate.ui.mood;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.omartitouhi.mindmate.data.local.MoodEntity;
import com.omartitouhi.mindmate.data.model.Mood;
import com.omartitouhi.mindmate.data.repository.MoodRepository;
import com.omartitouhi.mindmate.utils.Resource;

import java.util.List;

public class MoodViewModel extends AndroidViewModel {
    private final MoodRepository moodRepository;
    private final MutableLiveData<Resource<Mood>> moodState = new MutableLiveData<>();
    private final LiveData<List<MoodEntity>> localMoods;

    public MoodViewModel(@NonNull Application application) {
        super(application);
        moodRepository = new MoodRepository(application);
        localMoods = moodRepository.getLocalMoods();
    }

    public LiveData<Resource<Mood>> getMoodState() {
        return moodState;
    }

    public LiveData<List<MoodEntity>> getLocalMoods() {
        return localMoods;
    }

    public void saveMood(String mood, int stressScore, String note) {
        if (mood == null || mood.trim().isEmpty()) {
            moodState.setValue(Resource.error("Veuillez choisir une humeur."));
            return;
        }

        if (stressScore < 1 || stressScore > 10) {
            moodState.setValue(Resource.error("Le score de stress doit etre entre 1 et 10."));
            return;
        }

        String cleanNote = note == null ? "" : note.trim();
        moodRepository.saveMood(mood, stressScore, cleanNote, moodState::postValue);
    }

    public void clearState() {
        moodState.setValue(null);
    }
}
