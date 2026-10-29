package com.placementassist.institute.modules.internship.dto;

import com.placementassist.institute.common.enums.OpportunityMode;
import com.placementassist.institute.common.enums.OpportunityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateInternshipRequest(
        @NotBlank(message = "Company name is required")
        String company,

        @NotBlank(message = "Role is required")
        String role,

        Double stipend,
        Integer durationMonths,
        String location,

        @NotNull(message = "Mode is required")
        OpportunityMode mode,

        String eligibility,

        @NotNull(message = "Application deadline is required")
        LocalDate deadline,

        String applyUrl,
        String howToApply,
        String skillsRequired,
        OpportunityStatus status
) {}
