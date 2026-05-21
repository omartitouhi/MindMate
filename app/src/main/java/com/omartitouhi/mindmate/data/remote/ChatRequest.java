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
        this.safetyInstruction = "Réponds uniquement en français. Garde un ton humain, chaleureux, empathique et conversationnel. Reste dans un rôle de soutien bien-être général, sans diagnostic médical, évaluation de crise ni consignes de traitement. Encourage l’aide d’un professionnel quand c’est approprié.";
    }
}
