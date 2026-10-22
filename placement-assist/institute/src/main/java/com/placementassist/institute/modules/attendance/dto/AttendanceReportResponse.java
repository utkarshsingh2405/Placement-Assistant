package com.placementassist.institute.modules.attendance.dto;

import java.time.LocalDate;
import java.util.List;

public record AttendanceReportResponse(
        LocalDate from,
        LocalDate to,
        String branch,
        int totalStudentsEvaluated,
        int defaultersCount,
        Double averageAttendancePercentage,
        List<DefaulterStudentDto> defaulters
) {}
