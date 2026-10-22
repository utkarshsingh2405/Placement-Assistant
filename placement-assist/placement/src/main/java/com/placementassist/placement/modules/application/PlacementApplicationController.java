package com.placementassist.placement.modules.application;

import com.placementassist.shared.api.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PlacementApplicationController {

    private final PlacementApplicationService applicationService;

    public PlacementApplicationController(PlacementApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/applications")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PlacementApplicationEntity>>> getAllApplications() {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getAllApplications()));
    }

    @GetMapping("/drives/{driveId}/applications")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'COMPANY', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<PlacementApplicationEntity>>> getDriveApplications(
            @PathVariable Long driveId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PlacementApplicationEntity> apps = applicationService.getApplicationsForDrive(
                driveId, status, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"))
        );
        return ResponseEntity.ok(ApiResponse.ok(apps));
    }

    @PostMapping("/drives/{driveId}/apply")
    public ResponseEntity<ApiResponse<PlacementApplicationEntity>> apply(
            @PathVariable Long driveId,
            @RequestBody(required = false) Map<String, String> body,
            @RequestHeader(value = "X-User-RollNo", required = false) String rollNoHeader,
            @RequestHeader(value = "X-User-Email", required = false) String emailHeader,
            Principal principal) {

        String rollNo = (body != null && body.containsKey("rollNo")) ? body.get("rollNo") : rollNoHeader;
        String name = (body != null && body.containsKey("name")) ? body.get("name") : null;
        String email = (body != null && body.containsKey("email")) ? body.get("email") : emailHeader;

        if (rollNo == null || rollNo.isBlank()) {
            if (principal != null && principal.getName().contains("@")) {
                rollNo = principal.getName().split("@")[0].toUpperCase();
            } else {
                rollNo = "21CS001"; // Fallback demo roll
            }
        }

        PlacementApplicationEntity app = applicationService.apply(driveId, rollNo, name, email);
        return ResponseEntity.ok(ApiResponse.ok("Application submitted successfully", app));
    }

    @GetMapping("/applications/my")
    public ResponseEntity<ApiResponse<List<PlacementApplicationEntity>>> getMyApplications(
            @RequestParam(required = false) String rollNo,
            @RequestHeader(value = "X-User-RollNo", required = false) String rollNoHeader,
            Principal principal) {
        String effectiveRoll = rollNo != null ? rollNo : rollNoHeader;
        if (effectiveRoll == null || effectiveRoll.isBlank()) {
            if (principal != null) {
                effectiveRoll = principal.getName().split("@")[0].toUpperCase();
            } else {
                effectiveRoll = "21CS001";
            }
        }
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getMyApplications(effectiveRoll)));
    }

    @PutMapping("/applications/{id}/status")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PlacementApplicationEntity>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        String status = (String) body.get("status");
        Long nextStageId = body.containsKey("nextStageId") && body.get("nextStageId") != null
                ? Long.parseLong(body.get("nextStageId").toString())
                : null;
        PlacementApplicationEntity updated = applicationService.updateStatus(id, status, nextStageId);
        return ResponseEntity.ok(ApiResponse.ok("Status updated", updated));
    }
}
