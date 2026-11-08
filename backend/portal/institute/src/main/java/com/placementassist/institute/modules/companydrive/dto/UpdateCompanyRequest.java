package com.placementassist.institute.modules.companydrive.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyRequest {

    @NotBlank(message = "Company name cannot be blank")
    private String name;

    private String website;
    private String about;
    private String industry;
    private String headquarters;
    private String logoUrl;
}
