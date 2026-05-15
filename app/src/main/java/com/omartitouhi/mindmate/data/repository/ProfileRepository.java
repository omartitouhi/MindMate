package com.omartitouhi.mindmate.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.omartitouhi.mindmate.data.model.User;
import com.omartitouhi.mindmate.utils.Constants;
import com.omartitouhi.mindmate.utils.Resource;

import java.util.HashMap;
import java.util.Map;

public class ProfileRepository {
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final MutableLiveData<User> userProfile = new MutableLiveData<>();

    public LiveData<User> getUserProfile() {
        return userProfile;
    }

    public void loadUserProfile() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            userProfile.setValue(null);
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

    public void updateDisplayName(String displayName, AuthRepository.ResourceCallback<String> callback) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            callback.onResult(Resource.error("Utilisateur non connecte."));
            return;
        }

        callback.onResult(Resource.loading());
        String cleanName = displayName.trim();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(cleanName)
                .build();

        firebaseUser.updateProfile(request)
                .addOnSuccessListener(unused -> {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("displayName", cleanName);
                    updates.put("email", firebaseUser.getEmail() == null ? "" : firebaseUser.getEmail());
                    firestore.collection(Constants.FIRESTORE_USERS)
                            .document(firebaseUser.getUid())
                            .set(updates, SetOptions.merge())
                            .addOnSuccessListener(updateUnused -> {
                                loadUserProfile();
                                callback.onResult(Resource.success("Profil mis a jour."));
                            })
                            .addOnFailureListener(exception -> callback.onResult(Resource.error(getReadableError(exception))));
                })
                .addOnFailureListener(exception -> callback.onResult(Resource.error(getReadableError(exception))));
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
