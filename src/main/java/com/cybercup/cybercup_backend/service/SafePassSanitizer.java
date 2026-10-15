package com.cybercup.cybercup_backend.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SafePassSanitizer {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?<!\\d)(?:\\+?\\d[\\d\\s().-]{8,}\\d)(?!\\d)"
    );

    private static final Pattern CREDENTIAL_PATTERN = Pattern.compile(
            "(?i)\\b(?:api[_ -]?key|secret(?:[_ -]?key)?|" +
                    "password|passwd|access[_ -]?token|" +
                    "refresh[_ -]?token|auth[_ -]?token|token)\\b" +
                    "\\s*(?::|=|\\bis\\b)\\s*[\"']?" +
                    "([A-Za-z0-9_./+=-]{6,})"
    );

    public SanitizationResult sanitize(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                    "Prompt must not be empty."
            );
        }

        List<Finding> findings = new ArrayList<>();

        collectMatches(
                prompt, EMAIL_PATTERN, "EMAIL", findings
        );

        collectMatches(
                prompt, PHONE_PATTERN, "PHONE", findings
        );

        Matcher credentialMatcher =
                CREDENTIAL_PATTERN.matcher(prompt);

        while (credentialMatcher.find()) {
            findings.add(new Finding(
                    credentialMatcher.start(1),
                    credentialMatcher.end(1),
                    "CREDENTIAL"
            ));
        }

        // Process the earliest match first. For overlapping matches,
        // prefer the longer match.
        findings.sort(
                Comparator.comparingInt(Finding::start)
                        .thenComparing(
                                Comparator.comparingInt(
                                        (Finding f) ->
                                                f.end() - f.start()
                                ).reversed()
                        )
        );

        List<Finding> selected = new ArrayList<>();
        int lastEnd = -1;

        for (Finding finding : findings) {
            if (finding.start() >= lastEnd) {
                selected.add(finding);
                lastEnd = finding.end();
            }
        }

        Map<String, Integer> counts = new LinkedHashMap<>();
        Map<String, Integer> categoryIndexes =
                new LinkedHashMap<>();

        StringBuilder sanitized = new StringBuilder(prompt);

        // Replace from right to left so earlier indexes remain valid.
        for (int i = selected.size() - 1; i >= 0; i--) {
            Finding finding = selected.get(i);

            int index = categoryIndexes.merge(
                    finding.category(), 1, Integer::sum
            );

            counts.merge(finding.category(), 1, Integer::sum);

            String placeholder = "[REDACTED_"
                    + finding.category()
                    + "_" + index + "]";

            sanitized.replace(
                    finding.start(),
                    finding.end(),
                    placeholder
            );
        }

        return new SanitizationResult(
                sanitized.toString(),
                !selected.isEmpty(),
                Map.copyOf(counts)
        );
    }

    private void collectMatches(
            String prompt,
            Pattern pattern,
            String category,
            List<Finding> findings
    ) {
        Matcher matcher = pattern.matcher(prompt);

        while (matcher.find()) {
            findings.add(new Finding(
                    matcher.start(),
                    matcher.end(),
                    category
            ));
        }
    }

    private record Finding(
            int start,
            int end,
            String category
    ) {}

    public record SanitizationResult(
            String sanitizedPrompt,
            boolean sanitized,
            Map<String, Integer> categoryCounts
    ) {}
}