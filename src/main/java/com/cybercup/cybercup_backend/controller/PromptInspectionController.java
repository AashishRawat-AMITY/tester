
package com.cybercup.cybercup_backend.controller;

import com.cybercup.cybercup_backend.dto.PromptInspectionRequest;
import com.cybercup.cybercup_backend.dto.PromptInspectionResponse;
import com.cybercup.cybercup_backend.service.PromptInspectionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gateway")
public class PromptInspectionController {

    private final PromptInspectionService promptInspectionService;

    public PromptInspectionController(
            PromptInspectionService promptInspectionService) {
        this.promptInspectionService = promptInspectionService;
    }

    @PostMapping("/inspect")
    public ResponseEntity<PromptInspectionResponse> inspectPrompt(
            @Valid @RequestBody PromptInspectionRequest request,
            Authentication authentication) {

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            return ResponseEntity.status(401).build();
        }

        Long userId = jwtAuth.getToken().getClaim("userId");

        PromptInspectionResponse response =
                promptInspectionService.inspect(request, userId);

        return ResponseEntity.ok(response);
    }
}
