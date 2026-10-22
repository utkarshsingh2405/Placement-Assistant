package com.placementassist.placement.modules.result;

import com.placementassist.shared.api.ApiResponse;
import com.placementassist.shared.security.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PlacementResultController {

    private final PlacementResultService resultService;

    public PlacementResultController(PlacementResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping("/stages/{stageId}/results")
    public ResponseEntity<ApiResponse<List<PlacementResult>>> getStageResults(@PathVariable Long stageId) {
        return ResponseEntity.ok(ApiResponse.ok(resultService.getResultsForStage(stageId)));
    }

    @GetMapping("/stages/{stageId}/results/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStageResultStats(@PathVariable Long stageId) {
        return ResponseEntity.ok(ApiResponse.ok(resultService.getResultStats(stageId)));
    }

    @PostMapping("/stages/{stageId}/results")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PlacementResult>> markSingleResult(
            @PathVariable Long stageId,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-Role", required = false) String roleHeader,
            Principal principal) {

        Role actorRole = resolveActorRole(roleHeader);
        String rollNo = body.get("rollNo");
        String name = body.get("studentName");
        String status = body.get("resultStatus");
        String feedback = body.get("roundFeedback");
        String markedBy = principal != null ? principal.getName() : "TPO";

        PlacementResult result = resultService.markResult(actorRole, stageId, rollNo, name, status, feedback, markedBy);
        return ResponseEntity.ok(ApiResponse.ok("Evaluation result saved", result));
    }

    @PostMapping("/stages/{stageId}/results/bulk")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkMarkResults(
            @PathVariable Long stageId,
            @RequestBody List<Map<String, String>> items,
            @RequestHeader(value = "X-User-Role", required = false) String roleHeader,
            Principal principal) {

        Role actorRole = resolveActorRole(roleHeader);
        String markedBy = principal != null ? principal.getName() : "TPO";

        int count = resultService.bulkMarkResults(actorRole, stageId, items, markedBy);
        return ResponseEntity.ok(ApiResponse.ok("Batch results updated", Map.of("processedCount", count)));
    }

    private Role resolveActorRole(String roleHeader) {
        if (roleHeader != null && !roleHeader.isBlank()) {
            try {
                return Role.valueOf(roleHeader.toUpperCase().trim());
            } catch (Exception ignored) {}
        }
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null) {
            for (var a : auth.getAuthorities()) {
                String authStr = a.getAuthority();
                if (authStr.startsWith("ROLE_")) {
                    authStr = authStr.substring(5);
                }
                try {
                    return Role.valueOf(authStr.toUpperCase().trim());
                } catch (Exception ignored) {}
            }
        }
        return Role.STUDENT;
    }
}
