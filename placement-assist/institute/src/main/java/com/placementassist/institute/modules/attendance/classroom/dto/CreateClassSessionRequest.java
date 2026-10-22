package com.placementassist.institute.modules.attendance.classroom.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record CreateClassSessionRequest(
        @NotBlank(message = "Subject is required")
        String subject,

        @NotBlank(message = "Faculty name is required")
        String faculty,

        @NotNull(message = "Session date is required")
        LocalDate sessionDate,

        @NotBlank(message = "Start time is required")
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Start time must be HH:mm format")
        String startTime,

        @NotBlank(message = "End time is required")
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "End time must be HH:mm format")
        String endTime,

        @NotBlank(message = "Branch is required")
        String branch,

        @NotNull(message = "Year is required")
        @Min(value = 1, message = "Year must be at least 1")
        Integer year,

        @NotBlank(message = "Section is required")
        String section
) {}
