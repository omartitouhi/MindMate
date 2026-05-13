package com.omartitouhi.mindmate.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.omartitouhi.mindmate.data.model.User;
import com.omartitouhi.mindmate.utils.Resource;

public class AuthRepository {
    public interface ResourceCallback<T> {
        void onResult(Resource<T> resource);
    }

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public void login(String email, String password, ResourceCallback<FirebaseUser> callback) {
        callback.onResult(Resource.loading());
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> callback.onResult(Resource.success(authResult.getUser())))
                .addOnFailureListener(exception -> callback.onResult(Resource.error(getReadableError(exception))));
    }

    public void register(String displayName, String email, String password, ResourceCallback<User> callback) {
        callback.onResult(Resource.loading());
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        callback.onResult(Resource.error("Impossible de creer le compte utilisateur."));
                        return;
                    }

                    User user = new User(
                            firebaseUser.getUid(),
                            displayName,
                            email,
                            System.currentTimeMillis()
                    );

                    firestore.collection("users")
                            .document(firebaseUser.getUid())
                            .set(user)
                            .addOnSuccessListener(unused -> callback.onResult(Resource.success(user)))
                            .addOnFailureListener(exception -> callback.onResult(Resource.error(getReadableError(exception))));
                })
                .addOnFailureListener(exception -> callback.onResult(Resource.error(getReadableError(exception))));
    }

    public void resetPassword(String email, ResourceCallback<Void> callback) {
        callback.onResult(Resource.loading());
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> callback.onResult(Resource.success(null)))
                .addOnFailureListener(exception -> callback.onResult(Resource.error(getReadableError(exception))));
    }

    public void logout() {
        firebaseAuth.signOut();
    }

    private String getReadableError(Exception exception) {
        String message = exception.getLocalizedMessage();
        if (message == null || message.trim().isEmpty()) {
            return "Une erreur est survenue. Veuillez reessayer.";
        }
        return message;
    }
}
