package com.placementassist.institute.modules.attendance;

import com.placementassist.institute.common.enums.SessionType;
import com.placementassist.institute.modules.attendance.dto.AttendanceRecordResponse;
import com.placementassist.institute.modules.attendance.dto.AttendanceReportResponse;
import com.placementassist.institute.modules.attendance.dto.MarkAttendanceRequest;
import com.placementassist.institute.modules.attendance.dto.StudentAttendanceItemDto;
import com.placementassist.institute.modules.attendance.dto.StudentAttendanceStatsResponse;
import com.placementassist.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@Tag(name = "Attendance Reports & Analytics", description = "Student attendance statistics, percentages, and defaulter reports")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/student/{id}")
    @Operation(summary = "Get comprehensive attendance statistics and breakdown for a student")
    public ResponseEntity<ApiResponse<StudentAttendanceStatsResponse>> getStudentAttendanceStats(
            @PathVariable Long id) {
        StudentAttendanceStatsResponse stats = attendanceService.getStudentAttendanceStats(id);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/student/{id}/summary")
    @Operation(summary = "Get student attendance summary")
    public ResponseEntity<ApiResponse<StudentAttendanceStatsResponse>> getStudentAttendanceSummary(
            @PathVariable Long id) {
        StudentAttendanceStatsResponse stats = attendanceService.getStudentAttendanceStats(id);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/session/class/{id}")
    @Operation(summary = "Get classroom session attendance records")
    public ResponseEntity<ApiResponse<List<AttendanceRecordResponse>>> getClassSessionAttendance(
            @PathVariable Long id) {
        List<AttendanceRecordResponse> records = attendanceService.getClassAttendance(id);
        return ResponseEntity.ok(ApiResponse.ok(records));
    }

    @GetMapping("/session/crt/{id}")
    @Operation(summary = "Get CRT session attendance records")
    public ResponseEntity<ApiResponse<List<AttendanceRecordResponse>>> getCrtSessionAttendance(
            @PathVariable Long id) {
        List<AttendanceRecordResponse> records = attendanceService.getCrtAttendance(id);
        return ResponseEntity.ok(ApiResponse.ok(records));
    }

    public record BulkAttendancePayload(
            Long sessionId,
            SessionType sessionType,
            List<StudentAttendanceItemDto> items,
            List<StudentAttendanceItemDto> records
    ) {
        public List<StudentAttendanceItemDto> allRecords() {
            if (items != null && !items.isEmpty()) return items;
            if (records != null && !records.isEmpty()) return records;
            return List.of();
        }
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Bulk mark attendance for class or CRT session")
    public ResponseEntity<ApiResponse<List<AttendanceRecordResponse>>> bulkMark(
            @RequestBody BulkAttendancePayload payload,
            Authentication authentication) {
        String markedBy = authentication != null ? authentication.getName() : "DIRECTOR";
        List<StudentAttendanceItemDto> recs = payload.allRecords();
        MarkAttendanceRequest req = new MarkAttendanceRequest(recs);
        List<AttendanceRecordResponse> result;
        if (payload.sessionType() == SessionType.CRT) {
            result = attendanceService.markCrtAttendance(payload.sessionId(), req, markedBy);
        } else {
            result = attendanceService.markClassAttendance(payload.sessionId(), req, markedBy);
        }
        return ResponseEntity.ok(ApiResponse.ok("Attendance recorded successfully", result));
    }

    @GetMapping("/report")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Generate attendance report with overall average and list of defaulters (below 75%)")
    public ResponseEntity<ApiResponse<AttendanceReportResponse>> getAttendanceReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String branch) {
        AttendanceReportResponse report = attendanceService.getAttendanceReport(from, to, branch);
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @GetMapping(value = "/export/class", produces = "text/csv")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<String> exportClassAttendance() {
        String csv = "Session ID,Subject,Branch,Date,Faculty\n";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"class_attendance.csv\"")
                .body(csv);
    }

    @GetMapping(value = "/export/crt", produces = "text/csv")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<String> exportCrtAttendance() {
        String csv = "Session ID,Topic,Trainer,Date,Venue\n";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"crt_attendance.csv\"")
                .body(csv);
    }
}
