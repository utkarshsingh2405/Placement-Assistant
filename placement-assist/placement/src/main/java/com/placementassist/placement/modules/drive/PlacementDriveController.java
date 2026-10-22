package com.placementassist.placement.modules.drive;

import com.placementassist.shared.api.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/drives")
public class PlacementDriveController {

    private final PlacementDriveService driveService;

    public PlacementDriveController(PlacementDriveService driveService) {
        this.driveService = driveService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PlacementDrive>>> listDrives(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PlacementDrive> drives = driveService.searchDrives(search, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(ApiResponse.ok(drives));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PlacementDrive>>> getAllDrives() {
        return ResponseEntity.ok(ApiResponse.ok(driveService.getAllDrives()));
    }

    @GetMapping("/ongoing")
    public ResponseEntity<ApiResponse<List<PlacementDrive>>> getOngoingDrives() {
        return ResponseEntity.ok(ApiResponse.ok(driveService.getDrivesByStatus("ONGOING")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlacementDrive>> getDriveById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(driveService.getDriveById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PlacementDrive>> createDrive(@RequestBody PlacementDrive drive) {
        PlacementDrive created = driveService.createDrive(drive);
        return ResponseEntity.ok(ApiResponse.ok("Recruitment drive launched", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PlacementDrive>> updateDrive(@PathVariable Long id, @RequestBody PlacementDrive drive) {
        PlacementDrive updated = driveService.updateDrive(id, drive);
        return ResponseEntity.ok(ApiResponse.ok("Drive updated", updated));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PlacementDrive>> updateDriveStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        PlacementDrive updated = driveService.updateDriveStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Drive status changed", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDrive(@PathVariable Long id) {
        driveService.deleteDrive(id);
        return ResponseEntity.ok(ApiResponse.message("Recruitment drive deleted"));
    }
}
