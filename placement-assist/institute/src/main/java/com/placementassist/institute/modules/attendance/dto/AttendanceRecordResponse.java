package com.placementassist.institute.modules.attendance.dto;

import com.placementassist.institute.common.enums.AttendanceStatus;
import com.placementassist.institute.common.enums.SessionType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceRecordResponse(
        Long id,
        Long studentId,
        String studentRollNo,
        String studentName,
        SessionType sessionType,
        Long sessionId,
        String sessionTitle,
        LocalDate sessionDate,
        AttendanceStatus status,
        String markedBy,
        LocalDateTime createdAt
) {}
