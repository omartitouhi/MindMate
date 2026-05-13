package com.omartitouhi.mindmate.ui.journal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.data.local.JournalEntity;
import com.omartitouhi.mindmate.databinding.FragmentJournalDetailsBinding;
import com.omartitouhi.mindmate.utils.Resource;

import java.text.DateFormat;
import java.util.Date;

public class JournalDetailsFragment extends Fragment {
    public static final String ARG_ENTRY_ID = "entry_id";

    private FragmentJournalDetailsBinding binding;
    private JournalViewModel journalViewModel;
    private JournalEntity currentEntry;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentJournalDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        journalViewModel = new ViewModelProvider(this).get(JournalViewModel.class);
        journalViewModel.clearState();

        String entryId = getArguments() == null ? null : getArguments().getString(ARG_ENTRY_ID);
        if (entryId != null) {
            journalViewModel.getJournalEntry(entryId).observe(getViewLifecycleOwner(), entry -> {
                currentEntry = entry;
                renderEntry(entry);
            });
        }

        binding.updateButton.setOnClickListener(v -> {
            if (currentEntry != null) {
                journalViewModel.updateEntry(
                        currentEntry.getId(),
                        currentEntry.getCreatedAt(),
                        getText(binding.titleInput),
                        getText(binding.contentInput),
                        getText(binding.moodInput)
                );
            }
        });
        binding.deleteButton.setOnClickListener(v -> journalViewModel.deleteEntry(currentEntry));
        journalViewModel.getJournalState().observe(getViewLifecycleOwner(), this::renderState);
    }

    private void renderEntry(JournalEntity entry) {
        if (entry == null) {
            binding.messageText.setVisibility(View.VISIBLE);
            binding.messageText.setText(R.string.journal_entry_not_found);
            return;
        }
        binding.titleInput.setText(entry.getTitle());
        binding.contentInput.setText(entry.getContent());
        binding.moodInput.setText(entry.getMood());
        binding.dateText.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date(entry.getCreatedAt())));
    }

    private void renderState(Resource<?> state) {
        if (state == null) {
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.messageText.setVisibility(View.GONE);
            binding.updateButton.setEnabled(true);
            binding.deleteButton.setEnabled(true);
            return;
        }
        boolean loading = state.getStatus() == Resource.Status.LOADING;
        binding.loadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.updateButton.setEnabled(!loading);
        binding.deleteButton.setEnabled(!loading);

        boolean hasMessage = state.getStatus() == Resource.Status.SUCCESS || state.getStatus() == Resource.Status.ERROR;
        binding.messageText.setVisibility(hasMessage ? View.VISIBLE : View.GONE);
        binding.messageText.setText(state.getStatus() == Resource.Status.SUCCESS ? getString(R.string.journal_update_success) : state.getMessage());
        binding.messageText.setTextColor(requireContext().getColor(
                state.getStatus() == Resource.Status.SUCCESS
                        ? R.color.mindmate_secondary
                        : com.google.android.material.R.color.design_default_color_error
        ));
        if (state.getStatus() == Resource.Status.SUCCESS) {
            binding.getRoot().postDelayed(() -> requireActivity().getOnBackPressedDispatcher().onBackPressed(), 500);
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
