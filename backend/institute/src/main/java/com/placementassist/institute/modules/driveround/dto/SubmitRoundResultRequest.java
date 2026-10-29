package com.placementassist.institute.modules.driveround.dto;

import com.placementassist.institute.common.enums.RoundResultStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitRoundResultRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private Double score;

    @NotNull(message = "Result status is required")
    private RoundResultStatus result;

    private String remarks;
}
