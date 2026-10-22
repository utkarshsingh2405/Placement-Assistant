package com.placementassist.institute.modules.resume.dto;

import com.placementassist.institute.common.enums.ResumeStatus;
import jakarta.validation.constraints.NotNull;

public record ReviewResumeRequest(
        @NotNull(message = "Review status is required")
        ResumeStatus status,

        String feedback
) {}
