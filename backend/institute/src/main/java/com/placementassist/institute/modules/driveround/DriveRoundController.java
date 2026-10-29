package com.placementassist.institute.modules.driveround;

import com.placementassist.institute.modules.driveround.dto.*;
import com.placementassist.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "Drive Rounds & Evaluation", description = "Drive round stages, candidate score entry, bulk CSV evaluation, and funnel analytics")
public class DriveRoundController {

    private final DriveRoundService service;

    public DriveRoundController(DriveRoundService service) {
        this.service = service;
    }

    @GetMapping("/api/drives/{driveId}/rounds")
    @Operation(summary = "Get all evaluation rounds for a drive ordered by sequence")
    public ResponseEntity<ApiResponse<List<DriveRoundResponse>>> getRoundsForDrive(@PathVariable Long driveId) {
        List<DriveRoundResponse> response = service.getRoundsForDrive(driveId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/api/drives/{driveId}/rounds")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Add an evaluation round to a placement drive")
    public ResponseEntity<ApiResponse<DriveRoundResponse>> createRound(
            @PathVariable Long driveId,
            @Valid @RequestBody CreateRoundRequest request) {
        DriveRoundResponse response = service.createRound(driveId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Round created successfully", response));
    }

    @PutMapping("/api/rounds/{roundId}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Update round configuration")
    public ResponseEntity<ApiResponse<DriveRoundResponse>> updateRound(
            @PathVariable Long roundId,
            @Valid @RequestBody UpdateRoundRequest request) {
        DriveRoundResponse response = service.updateRound(roundId, request);
        return ResponseEntity.ok(ApiResponse.ok("Round updated successfully", response));
    }

    @DeleteMapping("/api/rounds/{roundId}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Delete an evaluation round (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteRound(@PathVariable Long roundId) {
        service.deleteRound(roundId);
        return ResponseEntity.ok(ApiResponse.message("Round deleted successfully"));
    }

    @GetMapping("/api/rounds/{roundId}/results")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Get candidate scores and results for a round")
    public ResponseEntity<ApiResponse<List<RoundResultResponse>>> getRoundResults(@PathVariable Long roundId) {
        List<RoundResultResponse> response = service.getResultsForRound(roundId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/api/rounds/{roundId}/results")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Submit or update individual candidate result for a round")
    public ResponseEntity<ApiResponse<RoundResultResponse>> submitResult(
            @PathVariable Long roundId,
            @Valid @RequestBody SubmitRoundResultRequest request) {
        RoundResultResponse response = service.submitResult(roundId, request);
        return ResponseEntity.ok(ApiResponse.ok("Result submitted successfully", response));
    }

    @PostMapping(value = "/api/rounds/{roundId}/results/bulk-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Bulk upload round results from CSV spreadsheet (RollNo, Score, Result, Remarks)")
    public ResponseEntity<ApiResponse<String>> bulkUploadResults(
            @PathVariable Long roundId,
            @RequestParam("file") MultipartFile file) {
        int processedCount = service.bulkUploadResultsCsv(roundId, file);
        return ResponseEntity.ok(ApiResponse.ok(
                String.format("Successfully processed %d student results from CSV", processedCount),
                String.format("%d records updated", processedCount)
        ));
    }

    @GetMapping("/api/drives/{driveId}/funnel")
    @Operation(summary = "Get recruitment drive candidate qualification funnel")
    public ResponseEntity<ApiResponse<DriveFunnelResponse>> getDriveFunnel(@PathVariable Long driveId) {
        DriveFunnelResponse response = service.getDriveFunnel(driveId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
