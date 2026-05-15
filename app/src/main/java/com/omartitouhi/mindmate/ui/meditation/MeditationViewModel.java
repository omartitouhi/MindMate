package com.omartitouhi.mindmate.ui.meditation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.List;

public class MeditationViewModel extends ViewModel {
    private final MutableLiveData<List<MeditationExercise>> exercises = new MutableLiveData<>(Arrays.asList(
            new MeditationExercise("Respiration 4-4-4", "Inspirez 4s, retenez 4s, expirez 4s.", 180000),
            new MeditationExercise("Relaxation rapide 2 minutes", "Une pause courte pour relacher les tensions.", 120000),
            new MeditationExercise("Focus breathing", "Recentrez votre attention avant une tache importante.", 300000),
            new MeditationExercise("Sleep relaxation", "Ralentissez le rythme pour preparer le sommeil.", 600000)
    ));

    public LiveData<List<MeditationExercise>> getExercises() {
        return exercises;
    }
}
