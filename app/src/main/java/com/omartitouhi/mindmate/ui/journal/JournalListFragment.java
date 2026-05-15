package com.omartitouhi.mindmate.ui.journal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.databinding.FragmentJournalListBinding;

public class JournalListFragment extends Fragment {
    private FragmentJournalListBinding binding;
    private JournalViewModel journalViewModel;
    private JournalAdapter journalAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentJournalListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        journalViewModel = new ViewModelProvider(this).get(JournalViewModel.class);
        journalAdapter = new JournalAdapter(entry -> {
            Bundle args = new Bundle();
            args.putString(JournalDetailsFragment.ARG_ENTRY_ID, entry.getId());
            NavHostFragment.findNavController(this).navigate(R.id.action_journalListFragment_to_journalDetailsFragment, args);
        });

        binding.journalRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.journalRecyclerView.setAdapter(journalAdapter);
        binding.addJournalButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_journalListFragment_to_addJournalFragment));

        journalViewModel.getJournalEntries().observe(getViewLifecycleOwner(), entries -> {
            journalAdapter.submitList(entries);
            boolean empty = entries == null || entries.isEmpty();
            binding.emptyText.setVisibility(empty ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
