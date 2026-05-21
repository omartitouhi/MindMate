package com.omartitouhi.mindmate.ui.meditation;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.omartitouhi.mindmate.R;

import java.util.Arrays;
import java.util.List;

public class MeditationViewModel extends AndroidViewModel {
    private final MutableLiveData<List<MeditationExercise>> exercises;

    public MeditationViewModel(@NonNull Application application) {
        super(application);
        exercises = new MutableLiveData<>(Arrays.asList(
                new MeditationExercise(application.getString(R.string.meditation_exercise_breathing_title), application.getString(R.string.meditation_exercise_breathing_description), 180000),
                new MeditationExercise(application.getString(R.string.meditation_exercise_quick_title), application.getString(R.string.meditation_exercise_quick_description), 120000),
                new MeditationExercise(application.getString(R.string.meditation_exercise_focus_title), application.getString(R.string.meditation_exercise_focus_description), 300000),
                new MeditationExercise(application.getString(R.string.meditation_exercise_sleep_title), application.getString(R.string.meditation_exercise_sleep_description), 600000)
        ));
    }

    public LiveData<List<MeditationExercise>> getExercises() {
        return exercises;
    }
}
