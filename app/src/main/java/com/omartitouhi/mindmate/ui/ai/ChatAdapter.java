package com.omartitouhi.mindmate.ui.ai;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.omartitouhi.mindmate.data.model.ChatMessage;
import com.omartitouhi.mindmate.databinding.ItemChatAssistantBinding;
import com.omartitouhi.mindmate.databinding.ItemChatUserBinding;

public class ChatAdapter extends ListAdapter<ChatMessage, RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_ASSISTANT = 2;

    public ChatAdapter() {
        super(DIFF_CALLBACK);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isUserMessage() ? VIEW_TYPE_USER : VIEW_TYPE_ASSISTANT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_USER) {
            return new UserMessageViewHolder(ItemChatUserBinding.inflate(inflater, parent, false));
        }
        return new AssistantMessageViewHolder(ItemChatAssistantBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = getItem(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else if (holder instanceof AssistantMessageViewHolder) {
            ((AssistantMessageViewHolder) holder).bind(message);
        }
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatUserBinding binding;

        UserMessageViewHolder(ItemChatUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatMessage message) {
            binding.messageText.setText(message.getContent());
        }
    }

    static class AssistantMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatAssistantBinding binding;

        AssistantMessageViewHolder(ItemChatAssistantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatMessage message) {
            binding.messageText.setText(message.getContent());
        }
    }

    private static final DiffUtil.ItemCallback<ChatMessage> DIFF_CALLBACK = new DiffUtil.ItemCallback<ChatMessage>() {
        @Override
        public boolean areItemsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            return oldItem.getContent().equals(newItem.getContent())
                    && oldItem.getRole().equals(newItem.getRole())
                    && oldItem.getTimestamp() == newItem.getTimestamp()
                    && oldItem.getStatus().equals(newItem.getStatus());
        }
    };
}
