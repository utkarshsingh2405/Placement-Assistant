package com.placementassist.institute.modules.resume.dto;

import com.placementassist.institute.common.enums.ResumeStatus;

import java.time.LocalDateTime;

public record ResumeResponse(
        Long id,
        Long studentId,
        String studentRollNo,
        String studentName,
        String fileUrl,
        Integer version,
        ResumeStatus status,
        Integer atsScore,
        AtsBreakdownDto atsBreakdown,
        String feedback,
        String reviewedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
