package com.placementassist.institute.modules.companydrive.dto;

import com.placementassist.institute.common.enums.DriveStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDriveRequest {

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotBlank(message = "Drive title is required")
    private String title;

    @NotNull(message = "Drive date is required")
    private LocalDate driveDate;

    @NotNull(message = "Last date to apply is required")
    private LocalDate lastDateToApply;

    @DecimalMin(value = "0.0", message = "Eligibility CGPA must be positive")
    @DecimalMax(value = "10.0", message = "Eligibility CGPA cannot exceed 10.0")
    @Builder.Default
    private Double eligibilityCgpa = 6.0;

    @NotEmpty(message = "At least one eligible branch must be specified")
    private List<String> eligibleBranches;

    @Min(value = 0, message = "Max backlogs cannot be negative")
    @Builder.Default
    private Integer maxBacklogs = 0;

    @NotEmpty(message = "At least one job role must be specified")
    private List<String> roles;

    private Double packageMin;
    private Double packageMax;
    private String location;

    @Builder.Default
    private DriveStatus status = DriveStatus.UPCOMING;

    private String description;
}
