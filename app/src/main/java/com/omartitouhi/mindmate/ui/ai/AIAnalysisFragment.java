package com.omartitouhi.mindmate.ui.ai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.data.local.JournalEntity;
import com.omartitouhi.mindmate.data.model.AiAnalysisResult;
import com.omartitouhi.mindmate.databinding.FragmentAiAnalysisBinding;
import com.omartitouhi.mindmate.utils.Resource;

import java.util.ArrayList;
import java.util.List;

public class AIAnalysisFragment extends Fragment {
    private FragmentAiAnalysisBinding binding;
    private AiViewModel aiViewModel;
    private final List<JournalEntity> journalEntries = new ArrayList<>();
    private JournalEntity selectedEntry;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAiAnalysisBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        aiViewModel = new ViewModelProvider(this).get(AiViewModel.class);
        aiViewModel.clearState();

        binding.analyzeButton.setOnClickListener(v -> aiViewModel.analyzeJournal(selectedEntry));
        aiViewModel.getJournalEntries().observe(getViewLifecycleOwner(), this::renderJournalEntries);
        aiViewModel.getAnalysisState().observe(getViewLifecycleOwner(), this::renderAnalysisState);
    }

    private void renderJournalEntries(List<JournalEntity> entries) {
        journalEntries.clear();
        if (entries != null) {
            journalEntries.addAll(entries);
        }

        List<String> titles = new ArrayList<>();
        for (JournalEntity entry : journalEntries) {
            titles.add(entry.getTitle());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                titles
        );
        binding.journalSelector.setAdapter(adapter);
        binding.emptyJournalText.setVisibility(journalEntries.isEmpty() ? View.VISIBLE : View.GONE);
        binding.analyzeButton.setEnabled(!journalEntries.isEmpty());

        binding.journalSelector.setOnItemClickListener((parent, itemView, position, id) -> {
            selectedEntry = journalEntries.get(position);
            binding.selectedExcerptText.setText(selectedEntry.getContent());
        });

        if (!journalEntries.isEmpty() && selectedEntry == null) {
            selectedEntry = journalEntries.get(0);
            binding.journalSelector.setText(selectedEntry.getTitle(), false);
            binding.selectedExcerptText.setText(selectedEntry.getContent());
        }
    }

    private void renderAnalysisState(Resource<AiAnalysisResult> state) {
        if (state == null) {
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.messageText.setVisibility(View.GONE);
            binding.resultCard.setVisibility(View.GONE);
            return;
        }

        boolean loading = state.getStatus() == Resource.Status.LOADING;
        binding.loadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.analyzeButton.setEnabled(!loading && !journalEntries.isEmpty());
        binding.messageText.setVisibility(state.getStatus() == Resource.Status.ERROR ? View.VISIBLE : View.GONE);
        binding.messageText.setText(state.getMessage());

        if (state.getStatus() == Resource.Status.SUCCESS && state.getData() != null) {
            renderResult(state.getData());
        }
    }

    private void renderResult(AiAnalysisResult result) {
        binding.resultCard.setVisibility(View.VISIBLE);
        binding.emotionText.setText(result.getMainEmotion());
        binding.stressText.setText(getString(R.string.ai_stress_result, result.getEstimatedStressLevel()));
        binding.summaryText.setText(result.getShortSummary());
        binding.adviceText.setText(result.getPersonalizedAdvice());
        binding.exerciseText.setText(result.getExerciseSuggestion());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
