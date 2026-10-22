package com.placementassist.institute.modules.attendance.dto;

import com.placementassist.institute.common.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

public record StudentAttendanceItemDto(
        @NotNull(message = "Student ID is required")
        Long studentId,

        @NotNull(message = "Attendance status is required")
        AttendanceStatus status
) {}
