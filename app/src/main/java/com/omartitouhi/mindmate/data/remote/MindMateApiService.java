package com.omartitouhi.mindmate.data.remote;

import com.omartitouhi.mindmate.data.model.AiMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MindMateApiService {
    @POST("ai/analyze")
    Call<AiMessage> analyzeJournal(@Body List<AiMessage> messages);

    @POST("ai/chat")
    Call<AiMessage> chat(@Body List<AiMessage> messages);
}
