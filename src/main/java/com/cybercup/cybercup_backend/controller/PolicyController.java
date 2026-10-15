package com.cybercup.cybercup_backend.controller;





import com.cybercup.cybercup_backend.entity.Policy;
import com.cybercup.cybercup_backend.service.PolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping
    public ResponseEntity<Policy> createPolicy(
            @RequestBody Policy policy) {

        Policy createdPolicy = policyService.createPolicy(policy);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdPolicy);
    }

    @GetMapping
    public ResponseEntity<List<Policy>> getAllPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(
            @PathVariable Long id) {

        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Policy> updatePolicy(
            @PathVariable Long id,
            @RequestBody Policy policy) {

        return ResponseEntity.ok(
                policyService.updatePolicy(id, policy)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Policy> updatePolicyStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {

        Boolean enabled = request.get("enabled");

        if (enabled == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                policyService.updatePolicyStatus(id, enabled)
        );
    }
}

