package com.placementassist.placement.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpportunityDto {
    private Long id;
    private String title;
    private String organization;
    private String type;
    private String location;
    private String applyUrl;
    private LocalDate deadline;
    private String stipendOrPackage;
    private String eligibility;
}
