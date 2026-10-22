package com.placementassist.placement.modules.stage;

import com.placementassist.shared.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlacementStageController {

    private final PlacementStageService stageService;

    public PlacementStageController(PlacementStageService stageService) {
        this.stageService = stageService;
    }

    @GetMapping("/drives/{driveId}/stages")
    public ResponseEntity<ApiResponse<List<PlacementStage>>> getStagesForDrive(@PathVariable Long driveId) {
        return ResponseEntity.ok(ApiResponse.ok(stageService.getStagesForDrive(driveId)));
    }

    @GetMapping("/stages/{id}")
    public ResponseEntity<ApiResponse<PlacementStage>> getStageById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(stageService.getStageById(id)));
    }

    @PutMapping("/stages/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PlacementStage>> updateStage(@PathVariable Long id, @RequestBody PlacementStage stage) {
        PlacementStage updated = stageService.updateStage(id, stage);
        return ResponseEntity.ok(ApiResponse.ok("Stage updated", updated));
    }

    @RequestMapping(value = "/stages/{id}/activate", method = {RequestMethod.POST, RequestMethod.PUT})
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PlacementStage>> activateStage(@PathVariable Long id) {
        PlacementStage activated = stageService.activateStage(id);
        return ResponseEntity.ok(ApiResponse.ok("Stage activated", activated));
    }

    @RequestMapping(value = "/stages/{id}/complete", method = {RequestMethod.POST, RequestMethod.PUT})
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PlacementStage>> completeStage(@PathVariable Long id) {
        PlacementStage completed = stageService.completeStage(id);
        return ResponseEntity.ok(ApiResponse.ok("Stage marked completed", completed));
    }

    @PostMapping("/drives/{driveId}/stages")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PlacementStage>> createStage(
            @PathVariable Long driveId,
            @RequestBody PlacementStage stage) {
        PlacementStage created = stageService.createStage(driveId, stage);
        return ResponseEntity.ok(ApiResponse.ok("Stage created successfully", created));
    }

    @DeleteMapping("/stages/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteStage(@PathVariable Long id) {
        stageService.deleteStage(id);
        return ResponseEntity.ok(ApiResponse.message("Stage deleted successfully"));
    }
}
