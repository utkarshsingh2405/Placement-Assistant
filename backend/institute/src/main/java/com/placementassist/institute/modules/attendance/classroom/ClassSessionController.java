package com.placementassist.institute.modules.attendance.classroom;

import com.placementassist.institute.modules.attendance.AttendanceService;
import com.placementassist.institute.modules.attendance.classroom.dto.ClassSessionResponse;
import com.placementassist.institute.modules.attendance.classroom.dto.CreateClassSessionRequest;
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
@RequestMapping({"/api/class/sessions", "/api/class-sessions"})
@Tag(name = "Classroom Attendance", description = "Academic classroom session management and bulk attendance marking")
public class ClassSessionController {

    private final AttendanceService attendanceService;

    public ClassSessionController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Create a new classroom academic session")
    public ResponseEntity<ApiResponse<ClassSessionResponse>> createSession(@Valid @RequestBody CreateClassSessionRequest request) {
        ClassSessionResponse response = attendanceService.createClassSession(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Class session created successfully", response));
    }

    @GetMapping
    @Operation(summary = "List classroom academic sessions with optional branch/year/section filters")
    public ResponseEntity<ApiResponse<List<ClassSessionResponse>>> getAllSessions(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String section) {
        List<ClassSessionResponse> sessions = attendanceService.getAllClassSessions(branch, year, section);
        return ResponseEntity.ok(ApiResponse.ok(sessions));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get classroom session by ID")
    public ResponseEntity<ApiResponse<ClassSessionResponse>> getSessionById(@PathVariable Long id) {
        ClassSessionResponse session = attendanceService.getClassSession(id);
        return ResponseEntity.ok(ApiResponse.ok(session));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Delete a classroom session")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable Long id) {
        attendanceService.deleteClassSession(id);
        return ResponseEntity.ok(ApiResponse.message("Class session deleted successfully"));
    }

    @PostMapping("/{id}/mark")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Bulk mark attendance for a classroom session")
    public ResponseEntity<ApiResponse<List<AttendanceRecordResponse>>> markAttendance(
            @PathVariable Long id,
            @Valid @RequestBody MarkAttendanceRequest request,
            Authentication authentication) {
        String markedBy = authentication != null ? authentication.getName() : "STAFF";
        List<AttendanceRecordResponse> records = attendanceService.markClassAttendance(id, request, markedBy);
        return ResponseEntity.ok(ApiResponse.ok("Class attendance marked successfully", records));
    }

    @GetMapping("/{id}/attendance")
    @Operation(summary = "Get attendance records for a classroom session")
    public ResponseEntity<ApiResponse<List<AttendanceRecordResponse>>> getAttendance(@PathVariable Long id) {
        List<AttendanceRecordResponse> records = attendanceService.getClassAttendance(id);
        return ResponseEntity.ok(ApiResponse.ok(records));
    }
}
