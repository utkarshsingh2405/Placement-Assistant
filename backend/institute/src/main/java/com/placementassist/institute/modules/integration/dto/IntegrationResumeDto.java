package com.placementassist.institute.modules.integration.dto;

import com.placementassist.institute.common.enums.ResumeStatus;

public record IntegrationResumeDto(
        Long studentId,
        String rollNo,
        String resumeUrl,
        Integer atsScore,
        ResumeStatus status,
        Integer version
) {}
