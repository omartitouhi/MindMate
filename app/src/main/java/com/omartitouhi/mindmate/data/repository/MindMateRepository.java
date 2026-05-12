package com.omartitouhi.mindmate.data.repository;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.omartitouhi.mindmate.data.local.JournalDao;
import com.omartitouhi.mindmate.data.local.MoodDao;
import com.omartitouhi.mindmate.data.model.JournalEntry;
import com.omartitouhi.mindmate.data.model.MoodEntry;
import com.omartitouhi.mindmate.data.remote.ApiClient;
import com.omartitouhi.mindmate.data.remote.MindMateApiService;

import java.util.List;

public class MindMateRepository {
    private final MoodDao moodDao;
    private final JournalDao journalDao;
    private final FirebaseFirestore firestore;
    private final MindMateApiService apiService;

    public MindMateRepository(MoodDao moodDao, JournalDao journalDao) {
        this.moodDao = moodDao;
        this.journalDao = journalDao;
        this.firestore = FirebaseFirestore.getInstance();
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<List<MoodEntry>> getMoodEntries() {
        return moodDao.getAllMoodEntries();
    }

    public LiveData<List<JournalEntry>> getJournalEntries() {
        return journalDao.getAllJournalEntries();
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }

    public MindMateApiService getApiService() {
        return apiService;
    }
}
