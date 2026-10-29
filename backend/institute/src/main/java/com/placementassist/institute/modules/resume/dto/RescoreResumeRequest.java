package com.placementassist.institute.modules.resume.dto;

import jakarta.validation.constraints.NotBlank;

public record RescoreResumeRequest(
        @NotBlank(message = "Target job description is required for re-scoring")
        String jobDescription
) {}
