package com.placementassist.institute.modules.attendance.crt.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CrtSessionResponse(
        Long id,
        String title,
        String topic,
        String trainer,
        LocalDate sessionDate,
        String startTime,
        String endTime,
        String venue,
        Integer totalEnrolledStudents,
        Integer attendedCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
