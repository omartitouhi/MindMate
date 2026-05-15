package com.omartitouhi.mindmate.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.data.model.StatisticsSummary;
import com.omartitouhi.mindmate.databinding.FragmentStatisticsBinding;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {
    private FragmentStatisticsBinding binding;
    private StatisticsViewModel statisticsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        statisticsViewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);
        configureChart();
        statisticsViewModel.getSummary().observe(getViewLifecycleOwner(), this::renderSummary);
    }

    private void renderSummary(StatisticsSummary summary) {
        if (summary == null) {
            return;
        }

        binding.emptyText.setVisibility(summary.isEmpty() ? View.VISIBLE : View.GONE);
        binding.contentContainer.setVisibility(summary.isEmpty() ? View.GONE : View.VISIBLE);

        if (summary.isEmpty()) {
            return;
        }

        binding.averageStressText.setText(getString(R.string.statistics_stress_value, summary.getAverageStress()));
        binding.journalCountText.setText(getString(R.string.statistics_count_value, summary.getJournalCount()));
        binding.frequentMoodText.setText(summary.getMostFrequentMood());
        binding.weeklyProgressText.setText(summary.getWeeklyProgress());
        renderMoodChart(summary.getMoodScoresLastSevenDays());
    }

    private void configureChart() {
        binding.moodChart.getDescription().setEnabled(false);
        binding.moodChart.getLegend().setEnabled(false);
        binding.moodChart.setNoDataText(getString(R.string.statistics_empty));
        binding.moodChart.setTouchEnabled(true);
        binding.moodChart.setDragEnabled(false);
        binding.moodChart.setScaleEnabled(false);
        binding.moodChart.getAxisRight().setEnabled(false);
        binding.moodChart.getAxisLeft().setAxisMinimum(0f);
        binding.moodChart.getAxisLeft().setAxisMaximum(5f);
        binding.moodChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.moodChart.getXAxis().setGranularity(1f);
        binding.moodChart.getXAxis().setDrawGridLines(false);
    }

    private void renderMoodChart(List<Float> scores) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            entries.add(new Entry(i, scores.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.statistics_mood_chart));
        dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.mindmate_primary));
        dataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.mindmate_secondary));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(ContextCompat.getColor(requireContext(), R.color.mindmate_primary_container));
        dataSet.setHighLightColor(Color.TRANSPARENT);

        binding.moodChart.setData(new LineData(dataSet));
        binding.moodChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
