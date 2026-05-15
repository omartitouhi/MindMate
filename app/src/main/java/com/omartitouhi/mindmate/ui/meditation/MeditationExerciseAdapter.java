package com.omartitouhi.mindmate.ui.meditation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omartitouhi.mindmate.databinding.ItemMeditationExerciseBinding;

import java.util.ArrayList;
import java.util.List;

public class MeditationExerciseAdapter extends RecyclerView.Adapter<MeditationExerciseAdapter.ExerciseViewHolder> {
    public interface OnExerciseClickListener {
        void onExerciseClick(MeditationExercise exercise);
    }

    private final List<MeditationExercise> exercises = new ArrayList<>();
    private final OnExerciseClickListener listener;

    public MeditationExerciseAdapter(OnExerciseClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<MeditationExercise> newExercises) {
        exercises.clear();
        exercises.addAll(newExercises);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMeditationExerciseBinding binding = ItemMeditationExerciseBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ExerciseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        holder.bind(exercises.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final ItemMeditationExerciseBinding binding;

        ExerciseViewHolder(ItemMeditationExerciseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(MeditationExercise exercise, OnExerciseClickListener listener) {
            binding.exerciseTitleText.setText(exercise.getTitle());
            binding.exerciseDescriptionText.setText(exercise.getDescription());
            binding.exerciseDurationText.setText(formatDuration(exercise.getDurationMillis()));
            binding.getRoot().setOnClickListener(v -> listener.onExerciseClick(exercise));
        }

        private String formatDuration(long durationMillis) {
            long minutes = durationMillis / 60000;
            if (minutes <= 0) {
                return "< 1 min";
            }
            return minutes + " min";
        }
    }
}
