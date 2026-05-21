package com.omartitouhi.mindmate.ui.ai;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.data.model.ChatMessage;
import com.omartitouhi.mindmate.data.repository.ChatRepository;
import com.omartitouhi.mindmate.utils.Resource;

import java.util.List;

public class ChatViewModel extends AndroidViewModel {
    private final ChatRepository chatRepository;
    private final LiveData<List<ChatMessage>> messages;
    private final MutableLiveData<Resource<ChatMessage>> chatState = new MutableLiveData<>();
    private volatile boolean sending;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        chatRepository = new ChatRepository(application);
        messages = chatRepository.getMessages();
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public LiveData<Resource<ChatMessage>> getChatState() {
        return chatState;
    }

    public boolean sendMessage(String content) {
        if (sending) {
            return false;
        }

        if (content == null || content.trim().isEmpty()) {
            chatState.setValue(Resource.error(getApplication().getString(R.string.ai_chat_error_empty_message)));
            return false;
        }

        sending = true;
        chatRepository.sendMessage(content.trim(), resource -> {
            if (resource != null && resource.getStatus() != Resource.Status.LOADING) {
                sending = false;
            }
            chatState.postValue(resource);
        });
        return true;
    }

    public void clearState() {
        chatState.setValue(null);
    }

    @Override
    protected void onCleared() {
        chatRepository.dispose();
        super.onCleared();
    }
}
