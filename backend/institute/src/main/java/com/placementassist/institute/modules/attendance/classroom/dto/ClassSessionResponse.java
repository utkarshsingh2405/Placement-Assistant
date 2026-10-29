package com.placementassist.institute.modules.attendance.classroom.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClassSessionResponse(
        Long id,
        String subject,
        String faculty,
        LocalDate sessionDate,
        String startTime,
        String endTime,
        String branch,
        Integer year,
        String section,
        Integer totalEnrolledStudents,
        Integer attendedCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
