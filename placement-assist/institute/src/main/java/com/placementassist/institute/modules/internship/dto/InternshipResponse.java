package com.placementassist.institute.modules.internship.dto;

import com.placementassist.institute.common.enums.OpportunityMode;
import com.placementassist.institute.common.enums.OpportunityStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record InternshipResponse(
        Long id,
        String company,
        String role,
        Double stipend,
        Integer durationMonths,
        String location,
        OpportunityMode mode,
        String eligibility,
        LocalDate deadline,
        String applyUrl,
        String howToApply,
        List<String> howToApplySteps,
        String skillsRequired,
        OpportunityStatus status,
        boolean bookmarked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
