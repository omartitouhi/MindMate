package com.omartitouhi.mindmate.ui.ai;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omartitouhi.mindmate.data.model.ChatMessage;
import com.omartitouhi.mindmate.data.repository.ChatRepository;
import com.omartitouhi.mindmate.utils.Resource;

import java.util.List;

public class ChatViewModel extends ViewModel {
    private final ChatRepository chatRepository = new ChatRepository();
    private final LiveData<List<ChatMessage>> messages = chatRepository.getMessages();
    private final MutableLiveData<Resource<ChatMessage>> chatState = new MutableLiveData<>();

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public LiveData<Resource<ChatMessage>> getChatState() {
        return chatState;
    }

    public void sendMessage(String content) {
        if (content == null || content.trim().isEmpty()) {
            chatState.setValue(Resource.error("Veuillez saisir un message."));
            return;
        }

        chatRepository.sendMessage(content.trim(), chatState::postValue);
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
