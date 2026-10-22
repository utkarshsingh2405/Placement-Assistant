package com.placementassist.placement.modules.attendance;

import com.placementassist.shared.api.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final QrCodeService qrCodeService;

    public AttendanceController(
            AttendanceService attendanceService,
            QrCodeService qrCodeService) {
        this.attendanceService = attendanceService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/stages/{stageId}/attendance")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'COMPANY', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Attendance>>> getStageAttendance(@PathVariable Long stageId) {
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.getAttendanceForStage(stageId)));
    }

    @GetMapping("/stages/{stageId}/attendance/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAttendanceStats(@PathVariable Long stageId) {
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.getAttendanceStats(stageId)));
    }

    @PostMapping("/stages/{stageId}/attendance/check-in")
    public ResponseEntity<ApiResponse<Attendance>> checkIn(
            @PathVariable Long stageId,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-RollNo", required = false) String rollNoHeader,
            Principal principal) {

        String rollNo = body.get("rollNo") != null ? body.get("rollNo") : (rollNoHeader != null ? rollNoHeader : "21CS001");
        String name = body.get("studentName");
        String mode = body.getOrDefault("mode", "QR_SCAN");
        String markedBy = principal != null ? principal.getName() : "Scanner";

        Attendance att = attendanceService.markCheckIn(stageId, rollNo, name, mode, markedBy);
        return ResponseEntity.ok(ApiResponse.ok("Check-in verified", att));
    }

    @GetMapping(value = "/stages/{stageId}/attendance/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getStageQrCode(@PathVariable Long stageId) {
        String payload = "CHECKIN:STAGE:" + stageId;
        byte[] qrImage = qrCodeService.generateQrCodePng(payload, 300, 300);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"stage-" + stageId + "-qr.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(qrImage);
    }

    @PostMapping("/attendance/{id}/toggle")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Attendance>> toggleAttendance(@PathVariable Long id) {
        Attendance updated = attendanceService.toggleAttendance(id);
        return ResponseEntity.ok(ApiResponse.ok("Attendance updated", updated));
    }
}
