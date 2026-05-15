package com.omartitouhi.mindmate.ui.settings;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.databinding.FragmentSettingsBinding;
import com.omartitouhi.mindmate.utils.Resource;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private SettingsViewModel settingsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        renderPreferences();

        binding.notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                settingsViewModel.setNotificationsEnabled(isChecked));
        binding.darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                settingsViewModel.setDarkModeEnabled(isChecked));
        binding.changeReminderTimeButton.setOnClickListener(v -> showReminderTimePicker());
        binding.deleteLocalDataButton.setOnClickListener(v -> confirmLocalDataDeletion());

        settingsViewModel.getSettingsState().observe(getViewLifecycleOwner(), this::renderState);
    }

    private void renderPreferences() {
        binding.notificationsSwitch.setChecked(settingsViewModel.areNotificationsEnabled());
        binding.darkModeSwitch.setChecked(settingsViewModel.isDarkModeEnabled());
        renderReminderTime();
    }

    private void showReminderTimePicker() {
        int hour = settingsViewModel.getReminderHour();
        int minute = settingsViewModel.getReminderMinute();
        new TimePickerDialog(requireContext(), (view, selectedHour, selectedMinute) -> {
            settingsViewModel.setReminderTime(selectedHour, selectedMinute);
            renderReminderTime();
        }, hour, minute, true).show();
    }

    private void renderReminderTime() {
        binding.reminderTimeText.setText(getString(
                R.string.settings_reminder_time_value,
                String.format(Locale.getDefault(), "%02d:%02d", settingsViewModel.getReminderHour(), settingsViewModel.getReminderMinute())
        ));
    }

    private void confirmLocalDataDeletion() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.action_delete_local_data)
                .setMessage(R.string.settings_delete_local_data_confirmation)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> settingsViewModel.deleteLocalData())
                .show();
    }

    private void renderState(Resource<String> state) {
        if (state == null) {
            return;
        }
        boolean loading = state.getStatus() == Resource.Status.LOADING;
        binding.loadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.deleteLocalDataButton.setEnabled(!loading);
        boolean hasMessage = state.getStatus() == Resource.Status.SUCCESS || state.getStatus() == Resource.Status.ERROR;
        binding.settingsMessageText.setVisibility(hasMessage ? View.VISIBLE : View.GONE);
        binding.settingsMessageText.setText(state.getStatus() == Resource.Status.SUCCESS ? state.getData() : state.getMessage());
        binding.settingsMessageText.setTextColor(requireContext().getColor(
                state.getStatus() == Resource.Status.SUCCESS
                        ? R.color.mindmate_secondary
                        : com.google.android.material.R.color.design_default_color_error
        ));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
