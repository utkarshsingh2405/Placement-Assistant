package com.placementassist.institute.modules.attendance.crt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record CreateCrtSessionRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Topic is required")
        String topic,

        @NotBlank(message = "Trainer name is required")
        String trainer,

        @NotNull(message = "Session date is required")
        LocalDate sessionDate,

        @NotBlank(message = "Start time is required")
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Start time must be HH:mm format")
        String startTime,

        @NotBlank(message = "End time is required")
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "End time must be HH:mm format")
        String endTime,

        String venue
) {}
