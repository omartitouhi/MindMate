package com.omartitouhi.mindmate.data.remote;

import com.google.gson.annotations.SerializedName;

public class N8nChatRequest {
    @SerializedName("projectId")
    private final String projectId;

    @SerializedName("dataBase")
    private final String dataBase;

    @SerializedName("collection")
    private final String collection;

    @SerializedName("userId")
    private final String userId;

    public N8nChatRequest(String userId) {
        this.projectId = "mindmateprojet";
        this.dataBase = "(default)";
        this.collection = "users";
        this.userId = userId;
    }
}
