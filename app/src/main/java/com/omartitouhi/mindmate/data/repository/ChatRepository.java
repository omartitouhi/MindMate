package com.omartitouhi.mindmate.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.omartitouhi.mindmate.data.model.ChatMessage;
import com.omartitouhi.mindmate.data.remote.ApiClient;
import com.omartitouhi.mindmate.data.remote.N8nChatRequest;
import com.omartitouhi.mindmate.utils.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {
    private static final String ERROR_REPLY = "Je n’arrive pas à répondre pour le moment. Réessaie dans quelques instants.";
    private static final String STATUS_SENT = "sent";
    private static final String STATUS_ERROR = "error";

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

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            messages.setValue(new ArrayList<>());
            return messages;
        }

        String userId = user.getUid();
        messagesRegistration = firestore.collection("users")
                .document(userId)
                .collection("chat_messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (snapshot == null || error != null) {
                        return;
                    }
                    messages.setValue(snapshot.toObjects(ChatMessage.class));
                });
        return messages;
    }

    public void sendMessage(String content, ChatCallback callback) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            callback.onResult(Resource.error("Veuillez vous connecter pour utiliser le chatbot."));
            return;
        }

        callback.onResult(Resource.loading());

        String userId = user.getUid();
        ChatMessage userMessage = createMessage(userId, ChatMessage.ROLE_USER, content, STATUS_SENT);
        saveMessage(userMessage)
                .addOnSuccessListener(unused -> callN8n(userId, callback))
                .addOnFailureListener(exception -> {
                    saveAssistantError(userId);
                    callback.onResult(Resource.error(getReadableError(exception)));
                });
    }

    private void callN8n(String userId, ChatCallback callback) {
        ApiClient.getN8nChatApiService()
                .sendMessage(new N8nChatRequest(userId))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            saveAssistantError(userId);
                            callback.onResult(Resource.error(ERROR_REPLY));
                            return;
                        }

                        try {
                            String rawResponse = response.body().string();
                            String botReply = extractBotReply(rawResponse);
                            if (botReply.trim().isEmpty()) {
                                botReply = ERROR_REPLY;
                            }

                            ChatMessage assistantMessage = createMessage(
                                    userId,
                                    ChatMessage.ROLE_ASSISTANT,
                                    botReply,
                                    STATUS_SENT
                            );
                            saveMessage(assistantMessage)
                                    .addOnSuccessListener(unused -> callback.onResult(Resource.success(assistantMessage)))
                                    .addOnFailureListener(exception -> callback.onResult(Resource.error(getReadableError(exception))));
                        } catch (IOException exception) {
                            saveAssistantError(userId);
                            callback.onResult(Resource.error(getReadableError(exception)));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        saveAssistantError(userId);
                        callback.onResult(Resource.error(ERROR_REPLY));
                    }
                });
    }

    public String extractBotReply(String rawResponse) {
        if (rawResponse == null) {
            return "";
        }

        String trimmed = rawResponse.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        try {
            JsonElement root = JsonParser.parseString(trimmed);
            String extracted = extractFromJson(root);
            if (!extracted.trim().isEmpty()) {
                return extracted;
            }
        } catch (Exception ignored) {
            // Plain text responses from n8n are valid; fall back to the raw body.
        }

        return trimmed;
    }

    private String extractFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return "";
        }

        if (element.isJsonPrimitive()) {
            return element.getAsString();
        }

        if (element.isJsonArray() && element.getAsJsonArray().size() > 0) {
            return extractFromJson(element.getAsJsonArray().get(0));
        }

        if (element.isJsonObject()) {
            if (element.getAsJsonObject().has("reply")) {
                return extractFromJson(element.getAsJsonObject().get("reply"));
            }
            if (element.getAsJsonObject().has("message")) {
                return extractFromJson(element.getAsJsonObject().get("message"));
            }
            if (element.getAsJsonObject().has("text")) {
                return extractFromJson(element.getAsJsonObject().get("text"));
            }
            if (element.getAsJsonObject().has("output")) {
                return extractFromJson(element.getAsJsonObject().get("output"));
            }
        }

        return "";
    }

    private void saveAssistantError(String userId) {
        saveMessage(createMessage(userId, ChatMessage.ROLE_ASSISTANT, ERROR_REPLY, STATUS_ERROR));
    }

    private ChatMessage createMessage(String userId, String role, String content, String status) {
        return new ChatMessage(
                UUID.randomUUID().toString(),
                userId,
                role,
                content,
                System.currentTimeMillis(),
                status
        );
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

    private String getReadableError(Throwable throwable) {
        String message = throwable.getLocalizedMessage();
        if (message == null || message.trim().isEmpty()) {
            return ERROR_REPLY;
        }
        return message;
    }
}
