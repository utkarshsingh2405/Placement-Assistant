package com.placementassist.institute.modules.attendance.crt;

import com.placementassist.institute.modules.attendance.AttendanceService;
import com.placementassist.institute.modules.attendance.crt.dto.CreateCrtSessionRequest;
import com.placementassist.institute.modules.attendance.crt.dto.CrtSessionResponse;
import com.placementassist.institute.modules.attendance.dto.AttendanceRecordResponse;
import com.placementassist.institute.modules.attendance.dto.MarkAttendanceRequest;
import com.placementassist.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/crt/sessions", "/api/crt-sessions"})
@Tag(name = "CRT Attendance", description = "Campus Recruitment Training session management and bulk attendance marking")
public class CrtSessionController {

    private final AttendanceService attendanceService;

    public CrtSessionController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Create a new CRT training session")
    public ResponseEntity<ApiResponse<CrtSessionResponse>> createSession(@Valid @RequestBody CreateCrtSessionRequest request) {
        CrtSessionResponse response = attendanceService.createCrtSession(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("CRT session created successfully", response));
    }

    @GetMapping
    @Operation(summary = "List all CRT training sessions")
    public ResponseEntity<ApiResponse<List<CrtSessionResponse>>> getAllSessions() {
        List<CrtSessionResponse> sessions = attendanceService.getAllCrtSessions();
        return ResponseEntity.ok(ApiResponse.ok(sessions));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get CRT training session by ID")
    public ResponseEntity<ApiResponse<CrtSessionResponse>> getSessionById(@PathVariable Long id) {
        CrtSessionResponse session = attendanceService.getCrtSession(id);
        return ResponseEntity.ok(ApiResponse.ok(session));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Delete a CRT session")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable Long id) {
        attendanceService.deleteCrtSession(id);
        return ResponseEntity.ok(ApiResponse.message("CRT session deleted successfully"));
    }

    @PostMapping("/{id}/mark")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Bulk mark attendance for a CRT session")
    public ResponseEntity<ApiResponse<List<AttendanceRecordResponse>>> markAttendance(
            @PathVariable Long id,
            @Valid @RequestBody MarkAttendanceRequest request,
            Authentication authentication) {
        String markedBy = authentication != null ? authentication.getName() : "STAFF";
        List<AttendanceRecordResponse> records = attendanceService.markCrtAttendance(id, request, markedBy);
        return ResponseEntity.ok(ApiResponse.ok("CRT attendance marked successfully", records));
    }

    @GetMapping("/{id}/attendance")
    @Operation(summary = "Get attendance records for a CRT session")
    public ResponseEntity<ApiResponse<List<AttendanceRecordResponse>>> getAttendance(@PathVariable Long id) {
        List<AttendanceRecordResponse> records = attendanceService.getCrtAttendance(id);
        return ResponseEntity.ok(ApiResponse.ok(records));
    }
}
