package com.placementassist.institute.modules.offcampusjob.dto;

import com.placementassist.institute.common.enums.OpportunityStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OffCampusJobResponse(
        Long id,
        String company,
        String role,
        Double packageLpa,
        String location,
        String batchEligible,
        String experience,
        String skillsRequired,
        LocalDate deadline,
        String applyUrl,
        String howToApply,
        List<String> howToApplySteps,
        OpportunityStatus status,
        boolean bookmarked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
