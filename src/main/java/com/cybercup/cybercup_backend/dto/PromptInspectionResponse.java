
package com.cybercup.cybercup_backend.dto;

public class PromptInspectionResponse {

    private String decision;
    private String message;
    private String requestId;
    private String sanitizedPrompt;

    public PromptInspectionResponse(
            String decision,
            String message,
            String requestId,
            String sanitizedPrompt) {
        this.decision = decision;
        this.message = message;
        this.requestId = requestId;
        this.sanitizedPrompt = sanitizedPrompt;
    }

    public String getDecision() {
        return decision;
    }

    public String getMessage() {
        return message;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getSanitizedPrompt() {
        return sanitizedPrompt;
    }
}
