package com.placementassist.institute.modules.companydrive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {

    private Long id;
    private String name;
    private String website;
    private String about;
    private String industry;
    private String headquarters;
    private String logoUrl;
    private LocalDateTime createdAt;
}
