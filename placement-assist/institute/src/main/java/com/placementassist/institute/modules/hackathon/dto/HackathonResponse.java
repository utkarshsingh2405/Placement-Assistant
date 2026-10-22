package com.placementassist.institute.modules.hackathon.dto;

import com.placementassist.institute.common.enums.OpportunityMode;
import com.placementassist.institute.common.enums.OpportunityStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record HackathonResponse(
        Long id,
        String name,
        String organizer,
        OpportunityMode mode,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate registrationDeadline,
        String prize,
        Integer teamSize,
        String location,
        String link,
        String description,
        String howToApply,
        List<String> howToApplySteps,
        OpportunityStatus status,
        boolean bookmarked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
