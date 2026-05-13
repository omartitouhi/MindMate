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
import com.omartitouhi.mindmate.databinding.FragmentAddJournalBinding;
import com.omartitouhi.mindmate.utils.Resource;

public class AddJournalFragment extends Fragment {
    private FragmentAddJournalBinding binding;
    private JournalViewModel journalViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddJournalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        journalViewModel = new ViewModelProvider(this).get(JournalViewModel.class);
        journalViewModel.clearState();

        binding.saveButton.setOnClickListener(v -> journalViewModel.addEntry(
                getText(binding.titleInput),
                getText(binding.contentInput),
                getText(binding.moodInput)
        ));

        journalViewModel.getJournalState().observe(getViewLifecycleOwner(), this::renderState);
    }

    private void renderState(Resource<?> state) {
        if (state == null) {
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.messageText.setVisibility(View.GONE);
            binding.saveButton.setEnabled(true);
            return;
        }
        boolean loading = state.getStatus() == Resource.Status.LOADING;
        binding.loadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.saveButton.setEnabled(!loading);

        boolean hasMessage = state.getStatus() == Resource.Status.SUCCESS || state.getStatus() == Resource.Status.ERROR;
        binding.messageText.setVisibility(hasMessage ? View.VISIBLE : View.GONE);
        binding.messageText.setText(state.getStatus() == Resource.Status.SUCCESS ? getString(R.string.journal_save_success) : state.getMessage());
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
