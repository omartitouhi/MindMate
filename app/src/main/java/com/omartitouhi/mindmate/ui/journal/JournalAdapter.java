package com.omartitouhi.mindmate.ui.journal;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.omartitouhi.mindmate.data.local.JournalEntity;
import com.omartitouhi.mindmate.databinding.ItemJournalEntryBinding;

import java.text.DateFormat;
import java.util.Date;

public class JournalAdapter extends ListAdapter<JournalEntity, JournalAdapter.JournalViewHolder> {
    public interface OnJournalClickListener {
        void onJournalClick(JournalEntity entry);
    }

    private final OnJournalClickListener listener;

    public JournalAdapter(OnJournalClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemJournalEntryBinding binding = ItemJournalEntryBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new JournalViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class JournalViewHolder extends RecyclerView.ViewHolder {
        private final ItemJournalEntryBinding binding;

        JournalViewHolder(ItemJournalEntryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(JournalEntity entry, OnJournalClickListener listener) {
            binding.titleText.setText(entry.getTitle());
            binding.excerptText.setText(buildExcerpt(entry.getContent()));
            binding.dateText.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date(entry.getCreatedAt())));
            binding.moodText.setText(entry.getMood());
            if (entry.isPendingDelete()) {
                binding.syncText.setText("Suppression en attente");
            } else {
                binding.syncText.setText(entry.isSynced() ? "Synchronise" : "En attente");
            }
            binding.getRoot().setOnClickListener(v -> listener.onJournalClick(entry));
        }

        private String buildExcerpt(String content) {
            if (content == null) {
                return "";
            }
            String cleanContent = content.trim();
            if (cleanContent.length() <= 96) {
                return cleanContent;
            }
            return cleanContent.substring(0, 96) + "...";
        }
    }

    private static final DiffUtil.ItemCallback<JournalEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<JournalEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull JournalEntity oldItem, @NonNull JournalEntity newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull JournalEntity oldItem, @NonNull JournalEntity newItem) {
            return oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getContent().equals(newItem.getContent())
                    && oldItem.getMood().equals(newItem.getMood())
                    && oldItem.getUpdatedAt() == newItem.getUpdatedAt()
                    && oldItem.isSynced() == newItem.isSynced()
                    && oldItem.isPendingDelete() == newItem.isPendingDelete();
        }
    };
}
