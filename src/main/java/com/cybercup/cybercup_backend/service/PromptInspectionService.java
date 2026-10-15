
//package com.cybercup.cybercup_backend.service;
//
//import com.cybercup.cybercup_backend.dto.PromptInspectionRequest;
//import com.cybercup.cybercup_backend.dto.PromptInspectionResponse;
//import com.cybercup.cybercup_backend.entity.Incident;
//import com.cybercup.cybercup_backend.repository.IncidentRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import com.cybercup.cybercup_backend.service.AuditLogService;
//import com.cybercup.cybercup_backend.repository.IncidentRepository;
//import org.springframework.stereotype.Service;
//import com.cybercup.cybercup_backend.repository.IncidentRepository;
//
//import java.util.UUID;
//import java.util.regex.Pattern;
//
//@Service
//public class PromptInspectionService {
//    private final IncidentRepository incidentRepository;
//    private final AuditLogService auditLogService;
//
//    public PromptInspectionService(
//            IncidentRepository incidentRepository,
//            AuditLogService auditLogService) {
//
//        this.incidentRepository = incidentRepository;
//        this.auditLogService = auditLogService;
//    }
//
//    private final AuditLogService auditLogService;
//    private static final Pattern EMAIL_PATTERN =
//            Pattern.compile(
//                    "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b"
//            );
//
//    private static final Pattern PHONE_PATTERN =
//            Pattern.compile("\\b\\d{10}\\b");
//
//    private static final Pattern API_KEY_PATTERN =
//            Pattern.compile(
//                    "(?i)\\b(api[_-]?key|secret[_-]?key)\\s*[:=]\\s*\\S+"
//            );
//
//    private final IncidentRepository incidentRepository;
//
//    public PromptInspectionService(IncidentRepository incidentRepository) {
//        this.incidentRepository = incidentRepository;
//    }
//
//    @Transactional
//    public PromptInspectionResponse inspect(
//            PromptInspectionRequest request,
//            Long userId) {
//
//        String prompt = request.getPrompt();
//        String requestId = UUID.randomUUID().toString();
//
//        if (API_KEY_PATTERN.matcher(prompt).find()) {
//            saveIncident(userId, "BLOCK", "API_KEY",
//                    "Potential API key or secret detected");
//
//            return new PromptInspectionResponse(
//                    "BLOCK",
//                    "Possible API key or secret detected.",
//                    requestId,
//                    null
//            );
//        }
//
//        if (EMAIL_PATTERN.matcher(prompt).find()
//                || PHONE_PATTERN.matcher(prompt).find()) {
//
//            String sanitizedPrompt = EMAIL_PATTERN.matcher(prompt)
//                    .replaceAll("[REDACTED_EMAIL]");
//
//            sanitizedPrompt = PHONE_PATTERN.matcher(sanitizedPrompt)
//                    .replaceAll("[REDACTED_PHONE]");
//
//            saveIncident(userId, "SANITIZE", "PII",
//                    "Potential personal information detected");
//
//            return new PromptInspectionResponse(
//                    "SANITIZE",
//                    "Potential personal information was redacted.",
//                    requestId,
//                    sanitizedPrompt
//            );
//        }
//
//        return new PromptInspectionResponse(
//                "ALLOW",
//                "No sensitive information detected by the current basic rules.",
//                requestId,
//                prompt
//        );
//    }
//
//    private void saveIncident(
//            Long userId,
//            String decision,
//            String category,
//            String description) {
//
//        Incident incident = new Incident(
//                userId, decision, category, description
//        );
//
//        incidentRepository.save(incident);
//    }
//}

package com.cybercup.cybercup_backend.service;

import com.cybercup.cybercup_backend.dto.PromptInspectionRequest;
import com.cybercup.cybercup_backend.dto.PromptInspectionResponse;
import com.cybercup.cybercup_backend.entity.Incident;
import com.cybercup.cybercup_backend.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class PromptInspectionService {

    private final IncidentRepository incidentRepository;
    private final AuditLogService auditLogService;

    public PromptInspectionService(
            IncidentRepository incidentRepository,
            AuditLogService auditLogService) {

        this.incidentRepository = incidentRepository;
        this.auditLogService = auditLogService;
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b"
            );

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("\\b\\d{10}\\b");

    private static final Pattern API_KEY_PATTERN =
            Pattern.compile(
                    "(?i)\\b(api[_-]?key|secret[_-]?key)\\s*[:=]\\s*\\S+"
            );

    @Transactional
    public PromptInspectionResponse inspect(
            PromptInspectionRequest request,
            Long userId) {

        String prompt = request.getPrompt();
        String requestId = UUID.randomUUID().toString();

        if (API_KEY_PATTERN.matcher(prompt).find()) {

            saveIncident(userId, "BLOCK", "API_KEY",
                    "Potential API key or secret detected");

            auditLogService.logAction(
                    userId,
                    "PROMPT_BLOCKED",
                    "category=API_KEY; decision=BLOCK"
            );

            return new PromptInspectionResponse(
                    "BLOCK",
                    "Possible API key or secret detected.",
                    requestId,
                    null
            );
        }

        if (EMAIL_PATTERN.matcher(prompt).find()
                || PHONE_PATTERN.matcher(prompt).find()) {

            String sanitizedPrompt = EMAIL_PATTERN.matcher(prompt)
                    .replaceAll("[REDACTED_EMAIL]");

            sanitizedPrompt = PHONE_PATTERN.matcher(sanitizedPrompt)
                    .replaceAll("[REDACTED_PHONE]");

            saveIncident(userId, "SANITIZE", "PII",
                    "Potential personal information detected");

            auditLogService.logAction(
                    userId,
                    "PROMPT_SANITIZED",
                    "category=PII; decision=SANITIZE"
            );

            return new PromptInspectionResponse(
                    "SANITIZE",
                    "Potential personal information was redacted.",
                    requestId,
                    sanitizedPrompt
            );
        }

        auditLogService.logAction(
                userId,
                "PROMPT_ALLOWED",
                "decision=ALLOW"
        );

        return new PromptInspectionResponse(
                "ALLOW",
                "No sensitive information detected by the current basic rules.",
                requestId,
                prompt
        );
    }

    private void saveIncident(
            Long userId,
            String decision,
            String category,
            String description) {

        Incident incident = new Incident(
                userId, decision, category, description
        );

        incidentRepository.save(incident);
    }
}
