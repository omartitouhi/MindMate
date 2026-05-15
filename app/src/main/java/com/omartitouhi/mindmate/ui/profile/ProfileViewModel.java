package com.omartitouhi.mindmate.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omartitouhi.mindmate.data.model.StatisticsSummary;
import com.omartitouhi.mindmate.data.model.User;
import com.omartitouhi.mindmate.data.repository.StatisticsRepository;
import com.omartitouhi.mindmate.utils.Constants;
import com.omartitouhi.mindmate.utils.Resource;

public class ProfileViewModel extends AndroidViewModel {
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final StatisticsRepository statisticsRepository;
    private final MutableLiveData<User> userProfile = new MutableLiveData<>();
    private final MutableLiveData<Resource<String>> profileState = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        statisticsRepository = new StatisticsRepository(application);
        loadUserProfile();
    }

    public LiveData<User> getUserProfile() {
        return userProfile;
    }

    public LiveData<Resource<String>> getProfileState() {
        return profileState;
    }

    public LiveData<StatisticsSummary> getStatisticsSummary() {
        return statisticsRepository.getSummary();
    }

    public void loadUserProfile() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            return;
        }

        String fallbackName = firebaseUser.getDisplayName() == null ? "" : firebaseUser.getDisplayName();
        String email = firebaseUser.getEmail() == null ? "" : firebaseUser.getEmail();
        userProfile.setValue(new User(firebaseUser.getUid(), fallbackName, email, 0));

        firestore.collection(Constants.FIRESTORE_USERS)
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String displayName = documentSnapshot.getString("displayName");
                    String storedEmail = documentSnapshot.getString("email");
                    Long createdAt = documentSnapshot.getLong("createdAt");
                    userProfile.setValue(new User(
                            firebaseUser.getUid(),
                            displayName == null || displayName.trim().isEmpty() ? fallbackName : displayName,
                            storedEmail == null || storedEmail.trim().isEmpty() ? email : storedEmail,
                            createdAt == null ? 0 : createdAt
                    ));
                });
    }

    public void updateDisplayName(String displayName) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            profileState.setValue(Resource.error("Utilisateur non connecte."));
            return;
        }
        if (displayName == null || displayName.trim().isEmpty()) {
            profileState.setValue(Resource.error("Veuillez saisir un nom."));
            return;
        }

        profileState.setValue(Resource.loading());
        String cleanName = displayName.trim();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(cleanName)
                .build();

        firebaseUser.updateProfile(request)
                .addOnSuccessListener(unused -> firestore.collection(Constants.FIRESTORE_USERS)
                        .document(firebaseUser.getUid())
                        .update("displayName", cleanName)
                        .addOnSuccessListener(updateUnused -> {
                            loadUserProfile();
                            profileState.setValue(Resource.success("Profil mis a jour."));
                        })
                        .addOnFailureListener(exception -> profileState.setValue(Resource.error(getReadableError(exception)))))
                .addOnFailureListener(exception -> profileState.setValue(Resource.error(getReadableError(exception))));
    }

    public void logout() {
        firebaseAuth.signOut();
    }

    private String getReadableError(Exception exception) {
        String message = exception.getLocalizedMessage();
        if (message == null || message.trim().isEmpty()) {
            return "Impossible de mettre a jour le profil.";
        }
        return message;
    }
}
