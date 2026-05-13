package com.omartitouhi.mindmate.ui.ai;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.omartitouhi.mindmate.data.local.JournalEntity;
import com.omartitouhi.mindmate.data.model.AiAnalysisResult;
import com.omartitouhi.mindmate.data.repository.AiRepository;
import com.omartitouhi.mindmate.utils.Resource;

import java.util.List;

public class AiViewModel extends AndroidViewModel {
    private final AiRepository aiRepository;
    private final MutableLiveData<Resource<AiAnalysisResult>> analysisState = new MutableLiveData<>();
    private final LiveData<List<JournalEntity>> journalEntries;

    public AiViewModel(@NonNull Application application) {
        super(application);
        aiRepository = new AiRepository(application);
        journalEntries = aiRepository.getJournalEntries();
    }

    public LiveData<List<JournalEntity>> getJournalEntries() {
        return journalEntries;
    }

    public LiveData<Resource<AiAnalysisResult>> getAnalysisState() {
        return analysisState;
    }

    public void analyzeJournal(JournalEntity journalEntry) {
        if (journalEntry == null) {
            analysisState.setValue(Resource.error("Veuillez selectionner une entree de journal."));
            return;
        }
        if (journalEntry.getContent() == null || journalEntry.getContent().trim().isEmpty()) {
            analysisState.setValue(Resource.error("Cette entree ne contient pas de texte a analyser."));
            return;
        }
        aiRepository.analyzeJournal(journalEntry, analysisState::postValue);
    }

    public void clearState() {
        analysisState.setValue(null);
    }
}
