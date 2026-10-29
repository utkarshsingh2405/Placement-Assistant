package com.placementassist.institute.modules.offcampusjob.dto;

import com.placementassist.institute.common.enums.OpportunityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateOffCampusJobRequest(
        @NotBlank(message = "Company name is required")
        String company,

        @NotBlank(message = "Role is required")
        String role,

        Double packageLpa,
        String location,
        String batchEligible,
        String experience,
        String skillsRequired,

        @NotNull(message = "Application deadline is required")
        LocalDate deadline,

        String applyUrl,
        String howToApply,
        OpportunityStatus status
) {}
