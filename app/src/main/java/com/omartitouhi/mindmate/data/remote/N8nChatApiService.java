package com.omartitouhi.mindmate.data.remote;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface N8nChatApiService {
    @POST("webhook/c71a525d-c1fb-4360-9af5-778b90343d09")
    Call<ResponseBody> sendMessage(@Body N8nChatRequest request);
}
