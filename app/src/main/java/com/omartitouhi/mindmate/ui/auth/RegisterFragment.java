package com.omartitouhi.mindmate.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.omartitouhi.mindmate.MainActivity;
import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.databinding.FragmentRegisterBinding;
import com.omartitouhi.mindmate.utils.Resource;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        authViewModel.clearState();

        binding.registerButton.setOnClickListener(v -> authViewModel.register(
                getText(binding.nameInput),
                getText(binding.emailInput),
                getText(binding.passwordInput),
                getText(binding.confirmPasswordInput)
        ));
        binding.loginButton.setOnClickListener(v -> {
            authViewModel.clearState();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        authViewModel.getAuthState().observe(getViewLifecycleOwner(), this::renderState);
    }

    private void renderState(Resource<String> state) {
        if (state == null) {
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.registerButton.setEnabled(true);
            binding.errorText.setVisibility(View.GONE);
            return;
        }

        boolean loading = state.getStatus() == Resource.Status.LOADING;
        binding.loadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.registerButton.setEnabled(!loading);

        boolean hasMessage = state.getStatus() == Resource.Status.ERROR || state.getStatus() == Resource.Status.SUCCESS;
        binding.errorText.setVisibility(hasMessage ? View.VISIBLE : View.GONE);
        binding.errorText.setText(state.getStatus() == Resource.Status.SUCCESS ? state.getData() : state.getMessage());
        binding.errorText.setTextColor(requireContext().getColor(
                state.getStatus() == Resource.Status.SUCCESS
                        ? R.color.mindmate_secondary
                        : com.google.android.material.R.color.design_default_color_error
        ));

        if (state.getStatus() == Resource.Status.SUCCESS) {
            binding.getRoot().postDelayed(() -> {
                startActivity(new Intent(requireContext(), MainActivity.class));
                requireActivity().finish();
            }, 600);
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
