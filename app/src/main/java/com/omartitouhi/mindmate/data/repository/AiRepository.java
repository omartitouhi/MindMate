package com.omartitouhi.mindmate.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.omartitouhi.mindmate.data.local.AppDatabase;
import com.omartitouhi.mindmate.data.local.JournalEntity;
import com.omartitouhi.mindmate.data.model.AiAnalysisResult;
import com.omartitouhi.mindmate.data.remote.AiAnalysisRequest;
import com.omartitouhi.mindmate.data.remote.AiApiService;
import com.omartitouhi.mindmate.data.remote.ApiClient;
import com.omartitouhi.mindmate.utils.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiRepository {
    public interface AnalysisCallback {
        void onResult(Resource<AiAnalysisResult> resource);
    }

    private final AiApiService aiApiService;
    private final LiveData<List<JournalEntity>> journalEntries;

    public AiRepository(Context context) {
        aiApiService = ApiClient.getAiApiService();
        journalEntries = AppDatabase.getInstance(context).journalDao().getAllJournalEntries();
    }

    public LiveData<List<JournalEntity>> getJournalEntries() {
        return journalEntries;
    }

    public void analyzeJournal(JournalEntity journalEntry, AnalysisCallback callback) {
        callback.onResult(Resource.loading());
        AiAnalysisRequest request = new AiAnalysisRequest(journalEntry.getId(), journalEntry.getContent());
        aiApiService.analyzeJournal(request).enqueue(new Callback<AiAnalysisResult>() {
            @Override
            public void onResponse(@NonNull Call<AiAnalysisResult> call, @NonNull Response<AiAnalysisResult> response) {
                AiAnalysisResult body = response.body();
                if (!response.isSuccessful() || body == null) {
                    callback.onResult(Resource.error("Impossible d'analyser cette entree pour le moment."));
                    return;
                }
                callback.onResult(Resource.success(body));
            }

            @Override
            public void onFailure(@NonNull Call<AiAnalysisResult> call, @NonNull Throwable throwable) {
                callback.onResult(Resource.error(getReadableError(throwable)));
            }
        });
    }

    private String getReadableError(Throwable throwable) {
        String message = throwable.getLocalizedMessage();
        if (message == null || message.trim().isEmpty()) {
            return "Le service d'analyse IA est indisponible.";
        }
        return message;
    }
}
