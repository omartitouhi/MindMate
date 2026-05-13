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
        this.safetyInstruction = "Provide supportive, non-medical wellness analysis. Do not provide medical diagnosis.";
    }
}
