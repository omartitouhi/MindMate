package com.omartitouhi.mindmate.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.omartitouhi.mindmate.AuthActivity;
import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.data.model.StatisticsSummary;
import com.omartitouhi.mindmate.data.model.User;
import com.omartitouhi.mindmate.databinding.FragmentProfileBinding;
import com.omartitouhi.mindmate.utils.Resource;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding.saveNameButton.setOnClickListener(v -> profileViewModel.updateDisplayName(getNameText()));
        binding.logoutButton.setOnClickListener(v -> {
            profileViewModel.logout();
            Intent intent = new Intent(requireContext(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), this::renderUser);
        profileViewModel.getStatisticsSummary().observe(getViewLifecycleOwner(), this::renderStats);
        profileViewModel.getProfileState().observe(getViewLifecycleOwner(), this::renderState);
    }

    private void renderUser(User user) {
        if (user == null) {
            return;
        }
        binding.nameInput.setText(user.getDisplayName());
        binding.emailText.setText(getString(R.string.profile_email_value, user.getEmail()));
    }

    private void renderStats(StatisticsSummary summary) {
        if (summary == null) {
            return;
        }
        binding.journalCountText.setText(getString(R.string.statistics_count_value, summary.getJournalCount()));
        binding.averageStressText.setText(getString(R.string.statistics_stress_value, summary.getAverageStress()));
        binding.frequentMoodText.setText(getString(R.string.profile_frequent_mood_value, summary.getMostFrequentMood()));
    }

    private void renderState(Resource<String> state) {
        if (state == null) {
            return;
        }
        boolean loading = state.getStatus() == Resource.Status.LOADING;
        binding.loadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.saveNameButton.setEnabled(!loading);
        boolean hasMessage = state.getStatus() == Resource.Status.SUCCESS || state.getStatus() == Resource.Status.ERROR;
        binding.messageText.setVisibility(hasMessage ? View.VISIBLE : View.GONE);
        binding.messageText.setText(state.getStatus() == Resource.Status.SUCCESS ? state.getData() : state.getMessage());
        binding.messageText.setTextColor(requireContext().getColor(
                state.getStatus() == Resource.Status.SUCCESS
                        ? R.color.mindmate_secondary
                        : com.google.android.material.R.color.design_default_color_error
        ));
    }

    private String getNameText() {
        return binding.nameInput.getText() == null ? "" : binding.nameInput.getText().toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
