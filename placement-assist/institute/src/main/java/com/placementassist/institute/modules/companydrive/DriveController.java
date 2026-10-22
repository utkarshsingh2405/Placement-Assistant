package com.placementassist.institute.modules.companydrive;

import com.placementassist.institute.common.enums.DriveStatus;
import com.placementassist.institute.modules.companydrive.dto.CreateDriveRequest;
import com.placementassist.institute.modules.companydrive.dto.DriveDetailResponse;
import com.placementassist.institute.modules.companydrive.dto.DriveResponse;
import com.placementassist.institute.modules.companydrive.dto.UpdateDriveRequest;
import com.placementassist.institute.modules.student.dto.StudentResponse;
import com.placementassist.shared.api.ApiResponse;
import com.placementassist.shared.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drives")
@Tag(name = "Company Drives", description = "Placement drives management, schedule, eligibility matching, and candidate funnels")
public class DriveController {

    private final CompanyDriveService service;

    public DriveController(CompanyDriveService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List recruitment drives with filters and pagination")
    public ResponseEntity<ApiResponse<PageResponse<DriveResponse>>> listDrives(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) DriveStatus status,
            @PageableDefault(size = 10, sort = "driveDate", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponse<DriveResponse> response = service.listDrives(search, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get list of all upcoming recruitment drives")
    public ResponseEntity<ApiResponse<List<DriveResponse>>> getUpcomingDrives() {
        List<DriveResponse> response = service.getUpcomingDrives();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get comprehensive drive details (includes company profile, evaluation rounds, and eligible students count)")
    public ResponseEntity<ApiResponse<DriveDetailResponse>> getDriveById(@PathVariable Long id) {
        DriveDetailResponse response = service.getDriveById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}/eligible-students")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Get list of students meeting eligibility criteria for this drive")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getEligibleStudents(@PathVariable Long id) {
        List<StudentResponse> response = service.getEligibleStudentsForDrive(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Schedule a new campus placement drive")
    public ResponseEntity<ApiResponse<DriveResponse>> createDrive(@Valid @RequestBody CreateDriveRequest request) {
        DriveResponse response = service.createDrive(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Drive created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Update placement drive details")
    public ResponseEntity<ApiResponse<DriveResponse>> updateDrive(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDriveRequest request) {
        DriveResponse response = service.updateDrive(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Drive updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Delete placement drive")
    public ResponseEntity<ApiResponse<Void>> deleteDrive(@PathVariable Long id) {
        service.deleteDrive(id);
        return ResponseEntity.ok(ApiResponse.message("Drive deleted successfully"));
    }
}
