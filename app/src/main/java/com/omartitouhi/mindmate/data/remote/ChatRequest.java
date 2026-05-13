package com.omartitouhi.mindmate.data.remote;

import com.google.gson.annotations.SerializedName;
import com.omartitouhi.mindmate.data.model.ChatMessage;

import java.util.List;

public class ChatRequest {
    @SerializedName("messages")
    private final List<ChatMessage> messages;

    @SerializedName("safety_instruction")
    private final String safetyInstruction;

    public ChatRequest(List<ChatMessage> messages) {
        this.messages = messages;
        this.safetyInstruction = "Stay in a general wellbeing support role. Do not provide medical diagnosis, crisis assessment, or treatment instructions. Encourage professional help when appropriate.";
    }
}
