package com.placementassist.institute.modules.driveround.dto;

import com.placementassist.institute.common.enums.RoundType;
import jakarta.validation.constraints.Min;
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

    private Long driveId;

    private RoundType roundType;
    private String roundName;

    @Min(value = 1, message = "Order number must be at least 1")
    private Integer orderNo;
    private Integer sequence;

    private LocalDate roundDate;
    private String venue;
    private String platformVenue;
    private Integer durationMins;
    private Double cutoff;
    private Double cutoffScore;
    private String instructions;

    public Integer getOrderNo() {
        if (orderNo != null && orderNo > 0) return orderNo;
        if (sequence != null && sequence > 0) return sequence;
        return 1;
    }

    public String getVenue() {
        if (venue != null && !venue.isBlank()) return venue;
        return platformVenue;
    }

    public Double getCutoff() {
        if (cutoff != null) return cutoff;
        return cutoffScore;
    }

    public RoundType getRoundType() {
        if (roundType != null) return roundType;
        if (roundName != null && !roundName.isBlank()) {
            try {
                return RoundType.valueOf(roundName.toUpperCase().replace(' ', '_'));
            } catch (Exception ignored) {}
        }
        return RoundType.ONLINE_TEST;
    }
}
