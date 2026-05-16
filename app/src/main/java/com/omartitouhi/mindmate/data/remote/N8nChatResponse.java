package com.omartitouhi.mindmate.data.remote;

import com.google.gson.annotations.SerializedName;

public class N8nChatResponse {
    @SerializedName("reply")
    private String reply;

    @SerializedName("message")
    private String message;

    @SerializedName("text")
    private String text;

    @SerializedName("output")
    private String output;

    public String getReply() {
        return reply;
    }

    public String getMessage() {
        return message;
    }

    public String getText() {
        return text;
    }

    public String getOutput() {
        return output;
    }
}
