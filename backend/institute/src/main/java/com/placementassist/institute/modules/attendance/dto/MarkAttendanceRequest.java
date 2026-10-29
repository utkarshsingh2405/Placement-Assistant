package com.placementassist.institute.modules.attendance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record MarkAttendanceRequest(
        @NotEmpty(message = "Attendance records list cannot be empty")
        @Valid
        List<StudentAttendanceItemDto> records
) {}
