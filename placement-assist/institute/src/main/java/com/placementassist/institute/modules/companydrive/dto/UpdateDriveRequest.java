package com.placementassist.institute.modules.companydrive.dto;

import com.placementassist.institute.common.enums.DriveStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class UpdateDriveRequest {

    @NotBlank(message = "Drive title cannot be blank")
    private String title;

    private LocalDate driveDate;

    private LocalDate lastDateToApply;

    @DecimalMin(value = "0.0", message = "Eligibility CGPA must be positive")
    @DecimalMax(value = "10.0", message = "Eligibility CGPA cannot exceed 10.0")
    private Double eligibilityCgpa;

    private List<String> eligibleBranches;

    @Min(value = 0, message = "Max backlogs cannot be negative")
    private Integer maxBacklogs;

    private List<String> roles;

    private Double packageMin;
    private Double packageMax;
    private String location;
    private DriveStatus status;
    private String description;
}
