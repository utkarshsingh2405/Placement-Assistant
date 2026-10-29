package com.placementassist.placement.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DriveRoundDto {
    private Long id;
    private Long driveId;
    private Integer orderNo;
    private String name;
    private String roundType;
    private String description;
}
