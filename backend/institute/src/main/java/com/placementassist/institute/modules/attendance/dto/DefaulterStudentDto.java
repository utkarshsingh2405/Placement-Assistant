package com.placementassist.institute.modules.attendance.dto;

public record DefaulterStudentDto(
        Long studentId,
        String rollNo,
        String name,
        String email,
        String branch,
        Integer year,
        long attendedSessions,
        long totalSessions,
        Double attendancePercentage
) {}
