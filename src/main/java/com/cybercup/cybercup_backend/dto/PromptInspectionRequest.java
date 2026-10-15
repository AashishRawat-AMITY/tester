package com.cybercup.cybercup_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PromptInspectionRequest {

    @NotBlank(message = "Prompt cannot be empty")
    @Size(max = 20000, message = "Prompt is too long")
    private String prompt;

    public PromptInspectionRequest() {
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
