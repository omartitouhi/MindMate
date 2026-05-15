package com.omartitouhi.mindmate.ui.meditation;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.databinding.FragmentMeditationBinding;

public class MeditationFragment extends Fragment {
    private FragmentMeditationBinding binding;
    private MeditationViewModel meditationViewModel;
    private MeditationExerciseAdapter exerciseAdapter;
    private MeditationExercise selectedExercise;
    private CountDownTimer countDownTimer;
    private long remainingMillis;
    private boolean running;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMeditationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        meditationViewModel = new ViewModelProvider(this).get(MeditationViewModel.class);
        exerciseAdapter = new MeditationExerciseAdapter(this::selectExercise);

        binding.exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.exercisesRecyclerView.setAdapter(exerciseAdapter);
        binding.startButton.setOnClickListener(v -> startTimer());
        binding.pauseButton.setOnClickListener(v -> pauseTimer());
        binding.resetButton.setOnClickListener(v -> resetTimer());

        meditationViewModel.getExercises().observe(getViewLifecycleOwner(), exercises -> {
            exerciseAdapter.submitList(exercises);
            if (selectedExercise == null && exercises != null && !exercises.isEmpty()) {
                selectExercise(exercises.get(0));
            }
        });
    }

    private void selectExercise(MeditationExercise exercise) {
        selectedExercise = exercise;
        remainingMillis = exercise.getDurationMillis();
        pauseTimer();
        binding.selectedExerciseText.setText(exercise.getTitle());
        binding.timerText.setText(formatTime(remainingMillis));
        binding.phaseText.setText(R.string.meditation_ready);
        binding.playerCard.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up));
    }

    private void startTimer() {
        if (selectedExercise == null || running) {
            return;
        }
        if (remainingMillis <= 0) {
            remainingMillis = selectedExercise.getDurationMillis();
        }
        running = true;
        binding.breathingAnimation.playAnimation();
        countDownTimer = new CountDownTimer(remainingMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingMillis = millisUntilFinished;
                binding.timerText.setText(formatTime(remainingMillis));
                binding.phaseText.setText(getPhaseText());
                binding.phaseText.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in));
            }

            @Override
            public void onFinish() {
                remainingMillis = 0;
                running = false;
                binding.timerText.setText(formatTime(0));
                binding.phaseText.setText(R.string.meditation_complete);
                binding.breathingAnimation.pauseAnimation();
            }
        }.start();
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        running = false;
        if (binding != null) {
            binding.breathingAnimation.pauseAnimation();
        }
    }

    private void resetTimer() {
        pauseTimer();
        if (selectedExercise != null) {
            remainingMillis = selectedExercise.getDurationMillis();
        }
        binding.timerText.setText(formatTime(remainingMillis));
        binding.phaseText.setText(R.string.meditation_ready);
        binding.breathingAnimation.setProgress(0f);
    }

    private int getPhaseText() {
        if (selectedExercise == null || !"Respiration 4-4-4".equals(selectedExercise.getTitle())) {
            return R.string.meditation_soft_breath;
        }

        long elapsedSeconds = (selectedExercise.getDurationMillis() - remainingMillis) / 1000;
        long cycleSecond = elapsedSeconds % 12;
        if (cycleSecond < 4) {
            return R.string.meditation_inhale;
        }
        if (cycleSecond < 8) {
            return R.string.meditation_hold;
        }
        return R.string.meditation_exhale;
    }

    private String formatTime(long millis) {
        long totalSeconds = Math.max(0, millis / 1000);
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    public void onDestroyView() {
        pauseTimer();
        super.onDestroyView();
        binding = null;
    }
}
