package com.placementassist.institute.modules.hackathon.dto;

import com.placementassist.institute.common.enums.OpportunityMode;
import com.placementassist.institute.common.enums.OpportunityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateHackathonRequest(
        @NotBlank(message = "Hackathon name is required")
        String name,

        @NotBlank(message = "Organizer is required")
        String organizer,

        @NotNull(message = "Mode is required")
        OpportunityMode mode,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        @NotNull(message = "Registration deadline is required")
        LocalDate registrationDeadline,

        String prize,
        Integer teamSize,
        String location,
        String link,
        String description,
        String howToApply,
        OpportunityStatus status
) {}
