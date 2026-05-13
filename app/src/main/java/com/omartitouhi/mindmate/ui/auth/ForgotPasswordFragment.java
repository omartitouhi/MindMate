package com.omartitouhi.mindmate.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.databinding.FragmentForgotPasswordBinding;
import com.omartitouhi.mindmate.utils.Resource;

public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding binding;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        authViewModel.clearState();

        binding.resetButton.setOnClickListener(v -> authViewModel.resetPassword(getText(binding.emailInput)));
        binding.loginButton.setOnClickListener(v -> {
            authViewModel.clearState();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        authViewModel.getAuthState().observe(getViewLifecycleOwner(), this::renderState);
    }

    private void renderState(Resource<String> state) {
        if (state == null) {
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.resetButton.setEnabled(true);
            binding.messageText.setVisibility(View.GONE);
            return;
        }

        boolean loading = state.getStatus() == Resource.Status.LOADING;
        binding.loadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.resetButton.setEnabled(!loading);
        binding.messageText.setVisibility(
                state.getStatus() == Resource.Status.ERROR || state.getStatus() == Resource.Status.SUCCESS
                        ? View.VISIBLE
                        : View.GONE
        );
        binding.messageText.setText(state.getStatus() == Resource.Status.SUCCESS ? state.getData() : state.getMessage());
        binding.messageText.setTextColor(requireContext().getColor(
                state.getStatus() == Resource.Status.SUCCESS
                        ? R.color.mindmate_secondary
                        : com.google.android.material.R.color.design_default_color_error
        ));
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
