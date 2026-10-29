package com.placementassist.institute.modules.driveround.dto;

import com.placementassist.institute.common.enums.RoundType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoundRequest {

    @NotNull(message = "Round type is required")
    private RoundType roundType;

    @NotNull(message = "Order sequence number is required")
    @Min(value = 1, message = "Order number must be at least 1")
    private Integer orderNo;

    private LocalDate roundDate;
    private String venue;
    private Integer durationMins;
    private Double cutoff;
    private String instructions;
}
