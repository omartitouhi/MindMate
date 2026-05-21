package com.omartitouhi.mindmate.data.remote;

import com.google.gson.annotations.SerializedName;

public class AiAnalysisRequest {
    @SerializedName("journal_entry_id")
    private final String journalEntryId;

    @SerializedName("journal_text")
    private final String journalText;

    @SerializedName("safety_instruction")
    private final String safetyInstruction;

    public AiAnalysisRequest(String journalEntryId, String journalText) {
        this.journalEntryId = journalEntryId;
        this.journalText = journalText;
        this.safetyInstruction = "Réponds uniquement en français, avec un ton chaleureux, humain et non médical. Propose une analyse de bien-être générale sans diagnostic médical.";
    }
}
