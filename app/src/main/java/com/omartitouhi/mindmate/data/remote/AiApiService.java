package com.omartitouhi.mindmate.data.remote;

import com.omartitouhi.mindmate.data.model.AiAnalysisResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AiApiService {
    @POST("ai/analyze-journal")
    Call<AiAnalysisResult> analyzeJournal(@Body AiAnalysisRequest request);

    @POST("ai/chat")
    Call<ChatResponse> chat(@Body ChatRequest request);
}
