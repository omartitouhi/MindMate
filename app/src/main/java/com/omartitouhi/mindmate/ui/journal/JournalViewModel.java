package com.omartitouhi.mindmate.ui.journal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.omartitouhi.mindmate.R;
import com.omartitouhi.mindmate.data.local.JournalEntity;
import com.omartitouhi.mindmate.data.model.JournalEntry;
import com.omartitouhi.mindmate.data.repository.JournalRepository;
import com.omartitouhi.mindmate.utils.Resource;

import java.util.List;

public class JournalViewModel extends AndroidViewModel {
    private final JournalRepository journalRepository;
    private final LiveData<List<JournalEntity>> journalEntries;
    private final MutableLiveData<Resource<JournalEntry>> journalState = new MutableLiveData<>();
    private final MutableLiveData<Resource<String>> syncState = new MutableLiveData<>();

    public JournalViewModel(@NonNull Application application) {
        super(application);
        journalRepository = new JournalRepository(application);
        journalEntries = journalRepository.getJournalEntries();
    }

    public LiveData<List<JournalEntity>> getJournalEntries() {
        return journalEntries;
    }

    public LiveData<JournalEntity> getJournalEntry(String id) {
        return journalRepository.getJournalEntry(id);
    }

    public LiveData<Resource<JournalEntry>> getJournalState() {
        return journalState;
    }

    public LiveData<Resource<String>> getSyncState() {
        return syncState;
    }

    public void addEntry(String title, String content, String mood) {
        if (!validate(title, content, mood)) {
            return;
        }
        journalRepository.addEntry(title.trim(), content.trim(), mood.trim(), journalState::postValue);
    }

    public void updateEntry(String id, long createdAt, String title, String content, String mood) {
        if (id == null || id.trim().isEmpty()) {
            journalState.setValue(Resource.error(getApplication().getString(R.string.journal_entry_not_found)));
            return;
        }
        if (!validate(title, content, mood)) {
            return;
        }
        journalRepository.updateEntry(id, createdAt, title.trim(), content.trim(), mood.trim(), journalState::postValue);
    }

    public void deleteEntry(JournalEntity entity) {
        if (entity == null) {
            journalState.setValue(Resource.error(getApplication().getString(R.string.journal_entry_not_found)));
            return;
        }
        journalRepository.deleteEntry(entity, journalState::postValue);
    }

    public void retrySync() {
        journalRepository.retrySync(syncState::postValue);
    }

    public void clearState() {
        journalState.setValue(null);
    }

    @Override
    protected void onCleared() {
        journalRepository.dispose();
        super.onCleared();
    }

    private boolean validate(String title, String content, String mood) {
        if (title == null || title.trim().isEmpty()) {
            journalState.setValue(Resource.error(getApplication().getString(R.string.journal_error_title_required)));
            return false;
        }
        if (content == null || content.trim().isEmpty()) {
            journalState.setValue(Resource.error(getApplication().getString(R.string.journal_error_content_required)));
            return false;
        }
        if (mood == null || mood.trim().isEmpty()) {
            journalState.setValue(Resource.error(getApplication().getString(R.string.journal_error_mood_required)));
            return false;
        }
        return true;
    }
}
