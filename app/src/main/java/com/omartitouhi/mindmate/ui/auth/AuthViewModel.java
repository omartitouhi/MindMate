package com.omartitouhi.mindmate.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omartitouhi.mindmate.data.repository.AuthRepository;
import com.omartitouhi.mindmate.utils.Resource;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository = new AuthRepository();
    private final MutableLiveData<Resource<String>> authState = new MutableLiveData<>();

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
                authState.setValue(Resource.success("Connexion reussie."));
            } else {
                authState.setValue(Resource.error(resource.getMessage()));
            }
        });
    }

    public void register(String displayName, String email, String password, String confirmPassword) {
        if (displayName == null || displayName.trim().isEmpty()) {
            authState.setValue(Resource.error("Veuillez saisir votre nom."));
            return;
        }

        if (!validateEmailAndPassword(email, password)) {
            return;
        }

        if (!password.equals(confirmPassword)) {
            authState.setValue(Resource.error("Les mots de passe ne correspondent pas."));
            return;
        }

        authRepository.register(displayName.trim(), email.trim(), password, resource -> {
            if (resource.getStatus() == Resource.Status.LOADING) {
                authState.setValue(Resource.loading());
            } else if (resource.getStatus() == Resource.Status.SUCCESS) {
                authState.setValue(Resource.success("Compte cree avec succes."));
            } else {
                authState.setValue(Resource.error(resource.getMessage()));
            }
        });
    }

    public void resetPassword(String email) {
        if (email == null || email.trim().isEmpty()) {
            authState.setValue(Resource.error("Veuillez saisir votre adresse email."));
            return;
        }

        authRepository.resetPassword(email.trim(), resource -> {
            if (resource.getStatus() == Resource.Status.LOADING) {
                authState.setValue(Resource.loading());
            } else if (resource.getStatus() == Resource.Status.SUCCESS) {
                authState.setValue(Resource.success("Email de reinitialisation envoye."));
            } else {
                authState.setValue(Resource.error(resource.getMessage()));
            }
        });
    }

    public void logout() {
        authRepository.logout();
        authState.setValue(Resource.success("Deconnexion reussie."));
    }

    public void clearState() {
        authState.setValue(null);
    }

    private boolean validateEmailAndPassword(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            authState.setValue(Resource.error("Veuillez saisir votre adresse email."));
            return false;
        }

        if (password == null || password.length() < 6) {
            authState.setValue(Resource.error("Le mot de passe doit contenir au moins 6 caracteres."));
            return false;
        }

        return true;
    }
}
