package com.omartitouhi.mindmate.ui.ai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.omartitouhi.mindmate.databinding.FragmentAiChatBinding;
import com.omartitouhi.mindmate.utils.Resource;

public class AIChatFragment extends Fragment {
    private FragmentAiChatBinding binding;
    private ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAiChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatViewModel.clearState();

        chatAdapter = new ChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        binding.messagesRecyclerView.setLayoutManager(layoutManager);
        binding.messagesRecyclerView.setAdapter(chatAdapter);

        binding.sendButton.setOnClickListener(v -> {
            chatViewModel.sendMessage(getMessageText());
            binding.messageInput.setText("");
        });

        chatViewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            chatAdapter.submitList(messages);
            binding.emptyText.setVisibility(messages == null || messages.isEmpty() ? View.VISIBLE : View.GONE);
            if (messages != null && !messages.isEmpty()) {
                binding.messagesRecyclerView.scrollToPosition(messages.size() - 1);
            }
        });
        chatViewModel.getChatState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                binding.loadingIndicator.setVisibility(View.GONE);
                binding.messageText.setVisibility(View.GONE);
                binding.sendButton.setEnabled(true);
                return;
            }

            boolean loading = state.getStatus() == Resource.Status.LOADING;
            binding.loadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.sendButton.setEnabled(!loading);
            binding.messageText.setVisibility(state.getStatus() == Resource.Status.ERROR ? View.VISIBLE : View.GONE);
            binding.messageText.setText(state.getMessage());
        });
    }

    private String getMessageText() {
        return binding.messageInput.getText() == null ? "" : binding.messageInput.getText().toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
