package com.placementassist.institute.modules.attendance.dto;

import java.util.List;

public record StudentAttendanceStatsResponse(
        Long studentId,
        String rollNo,
        String name,
        String branch,
        Integer year,
        long totalSessions,
        long attendedSessions,
        Double overallPercentage,
        long crtTotal,
        long crtAttended,
        Double crtPercentage,
        long classTotal,
        long classAttended,
        Double classPercentage,
        boolean isDefaulter,
        List<AttendanceRecordResponse> recentRecords
) {}
