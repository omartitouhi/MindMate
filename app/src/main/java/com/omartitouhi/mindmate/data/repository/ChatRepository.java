package com.omartitouhi.mindmate.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.android.gms.tasks.Task;
import com.omartitouhi.mindmate.data.model.ChatMessage;
import com.omartitouhi.mindmate.data.remote.ApiClient;
import com.omartitouhi.mindmate.data.remote.ChatRequest;
import com.omartitouhi.mindmate.data.remote.ChatResponse;
import com.omartitouhi.mindmate.utils.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {
    public interface ChatCallback {
        void onResult(Resource<ChatMessage> resource);
    }

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private ListenerRegistration messagesRegistration;

    public LiveData<List<ChatMessage>> getMessages() {
        if (messagesRegistration != null) {
            return messages;
        }
        String userId = getUserId();
        messagesRegistration = firestore.collection("users")
                .document(userId)
                .collection("chat_messages")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (snapshot == null || error != null) {
                        return;
                    }
                    messages.setValue(snapshot.toObjects(ChatMessage.class));
                });
        return messages;
    }

    public void sendMessage(String content, List<ChatMessage> currentMessages, ChatCallback callback) {
        callback.onResult(Resource.loading());

        String userId = getUserId();
        ChatMessage userMessage = new ChatMessage(
                UUID.randomUUID().toString(),
                userId,
                ChatMessage.ROLE_USER,
                content,
                System.currentTimeMillis()
        );

        saveMessage(userMessage)
                .addOnFailureListener(exception -> callback.onResult(Resource.error(getReadableError(exception))));

        List<ChatMessage> requestMessages = new ArrayList<>();
        if (currentMessages != null) {
            requestMessages.addAll(currentMessages);
        }
        requestMessages.add(userMessage);

        ApiClient.getAiApiService().chat(new ChatRequest(requestMessages)).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                ChatResponse body = response.body();
                if (!response.isSuccessful() || body == null || body.getReply() == null || body.getReply().trim().isEmpty()) {
                    callback.onResult(Resource.error("Impossible d'obtenir une reponse IA pour le moment."));
                    return;
                }

                ChatMessage assistantMessage = new ChatMessage(
                        UUID.randomUUID().toString(),
                        userId,
                        ChatMessage.ROLE_ASSISTANT,
                        body.getReply(),
                        System.currentTimeMillis()
                );
                saveMessage(assistantMessage)
                        .addOnFailureListener(exception -> callback.onResult(Resource.error(getReadableError(exception))));
                callback.onResult(Resource.success(assistantMessage));
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable throwable) {
                callback.onResult(Resource.error(getReadableError(throwable)));
            }
        });
    }

    private Task<Void> saveMessage(ChatMessage message) {
        return firestore.collection("users")
                .document(message.getUserId())
                .collection("chat_messages")
                .document(message.getId())
                .set(message);
    }

    public void dispose() {
        if (messagesRegistration != null) {
            messagesRegistration.remove();
            messagesRegistration = null;
        }
    }

    private String getUserId() {
        return firebaseAuth.getCurrentUser() != null
                ? firebaseAuth.getCurrentUser().getUid()
                : "anonymous";
    }

    private String getReadableError(Throwable throwable) {
        String message = throwable.getLocalizedMessage();
        if (message == null || message.trim().isEmpty()) {
            return "Le chatbot est indisponible pour le moment.";
        }
        return message;
    }
}
