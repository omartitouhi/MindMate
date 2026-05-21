package com.omartitouhi.mindmate.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.data.repository.AuthRepository;
import com.omartitouhi.mindmate.utils.Resource;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<Resource<String>> authState = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }

    public LiveData<Resource<String>> getAuthState() {
        return authState;
    }

    public boolean isUserLoggedIn() {
        return authRepository.isUserLoggedIn();
    }

    public void login(String email, String password) {
        if (!validateEmailAndPassword(email, password)) {
            return;
        }

        authRepository.login(email.trim(), password, resource -> {
            if (resource.getStatus() == Resource.Status.LOADING) {
                authState.setValue(Resource.loading());
            } else if (resource.getStatus() == Resource.Status.SUCCESS) {
                authState.setValue(Resource.success(getApplication().getString(R.string.auth_success_login)));
            } else {
                authState.setValue(Resource.error(resource.getMessage()));
            }
        });
    }

    public void register(String displayName, String email, String password, String confirmPassword) {
        if (displayName == null || displayName.trim().isEmpty()) {
            authState.setValue(Resource.error(getApplication().getString(R.string.error_name_required)));
            return;
        }

        if (!validateEmailAndPassword(email, password)) {
            return;
        }

        if (!password.equals(confirmPassword)) {
            authState.setValue(Resource.error(getApplication().getString(R.string.error_passwords_mismatch)));
            return;
        }

        authRepository.register(displayName.trim(), email.trim(), password, resource -> {
            if (resource.getStatus() == Resource.Status.LOADING) {
                authState.setValue(Resource.loading());
            } else if (resource.getStatus() == Resource.Status.SUCCESS) {
                authState.setValue(Resource.success(getApplication().getString(R.string.auth_success_register)));
            } else {
                authState.setValue(Resource.error(resource.getMessage()));
            }
        });
    }

    public void resetPassword(String email) {
        if (email == null || email.trim().isEmpty()) {
            authState.setValue(Resource.error(getApplication().getString(R.string.error_email_required)));
            return;
        }

        authRepository.resetPassword(email.trim(), resource -> {
            if (resource.getStatus() == Resource.Status.LOADING) {
                authState.setValue(Resource.loading());
            } else if (resource.getStatus() == Resource.Status.SUCCESS) {
                authState.setValue(Resource.success(getApplication().getString(R.string.auth_success_reset)));
            } else {
                authState.setValue(Resource.error(resource.getMessage()));
            }
        });
    }

    public void logout() {
        authRepository.logout();
        authState.setValue(Resource.success(getApplication().getString(R.string.auth_success_logout)));
    }

    public void clearState() {
        authState.setValue(null);
    }

    private boolean validateEmailAndPassword(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            authState.setValue(Resource.error(getApplication().getString(R.string.error_email_required)));
            return false;
        }

        if (password == null || password.length() < 6) {
            authState.setValue(Resource.error(getApplication().getString(R.string.error_password_too_short)));
            return false;
        }

        return true;
    }
}
