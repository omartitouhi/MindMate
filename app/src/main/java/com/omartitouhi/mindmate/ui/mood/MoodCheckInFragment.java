package com.omartitouhi.mindmate.ui.mood;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.data.model.Mood;
import com.omartitouhi.mindmate.databinding.FragmentMoodCheckInBinding;
import com.omartitouhi.mindmate.utils.Resource;

public class MoodCheckInFragment extends Fragment {
    private FragmentMoodCheckInBinding binding;
    private MoodViewModel moodViewModel;
    private String selectedMood;
    private int selectedStressScore = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMoodCheckInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        moodViewModel = new ViewModelProvider(this).get(MoodViewModel.class);
        moodViewModel.clearState();

        bindMoodCard(binding.happyCard, "Happy");
        bindMoodCard(binding.calmCard, "Calm");
        bindMoodCard(binding.sadCard, "Sad");
        bindMoodCard(binding.angryCard, "Angry");
        bindMoodCard(binding.stressedCard, "Stressed");
        bindMoodCard(binding.tiredCard, "Tired");

        binding.stressSlider.addOnChangeListener((slider, value, fromUser) -> {
            selectedStressScore = Math.round(value);
            binding.stressValueText.setText(getString(R.string.mood_stress_value, selectedStressScore));
        });

        binding.saveMoodButton.setOnClickListener(v -> moodViewModel.saveMood(
                selectedMood,
                selectedStressScore,
                getText(binding.noteInput)
        ));

        moodViewModel.getMoodState().observe(getViewLifecycleOwner(), this::renderState);
    }

    private void bindMoodCard(MaterialCardView card, String mood) {
        card.setOnClickListener(v -> {
            selectedMood = mood;
            renderMoodSelection(card);
        });
    }

    private void renderMoodSelection(MaterialCardView selectedCard) {
        resetCard(binding.happyCard);
        resetCard(binding.calmCard);
        resetCard(binding.sadCard);
        resetCard(binding.angryCard);
        resetCard(binding.stressedCard);
        resetCard(binding.tiredCard);

        selectedCard.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.mood_selected_stroke_width));
        selectedCard.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.mindmate_primary));
        selectedCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.mindmate_primary_container));
    }

    private void resetCard(MaterialCardView card) {
        card.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.mood_card_stroke_width));
        card.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.mindmate_outline));
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        card.setRippleColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.mindmate_primary_container)));
    }

    private void renderState(Resource<Mood> state) {
        if (state == null) {
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.messageText.setVisibility(View.GONE);
            binding.saveMoodButton.setEnabled(true);
            return;
        }

        boolean loading = state.getStatus() == Resource.Status.LOADING;
        binding.loadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.saveMoodButton.setEnabled(!loading);

        boolean hasMessage = state.getStatus() == Resource.Status.ERROR || state.getStatus() == Resource.Status.SUCCESS;
        binding.messageText.setVisibility(hasMessage ? View.VISIBLE : View.GONE);
        binding.messageText.setText(state.getStatus() == Resource.Status.SUCCESS
                ? getString(R.string.mood_save_success)
                : state.getMessage());
        binding.messageText.setTextColor(ContextCompat.getColor(
                requireContext(),
                state.getStatus() == Resource.Status.SUCCESS
                        ? R.color.mindmate_secondary
                        : com.google.android.material.R.color.design_default_color_error
        ));

        if (state.getStatus() == Resource.Status.SUCCESS) {
            binding.noteInput.setText("");
        }
    }

    private String getText(com.google.android.material.textfield.TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
