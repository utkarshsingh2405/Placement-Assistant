package com.placementassist.institute.modules.internship;

import com.placementassist.institute.common.BaseEntity;
import com.placementassist.institute.common.enums.OpportunityMode;
import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.listing.ListingItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "internships")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Internship extends BaseEntity implements ListingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company", nullable = false)
    private String company;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "stipend")
    private Double stipend; // Monthly stipend in INR (e.g., 45000)

    @Column(name = "duration_months")
    private Integer durationMonths;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private OpportunityMode mode;

    @Column(name = "eligibility")
    private String eligibility;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "apply_url")
    private String applyUrl;

    @Column(name = "how_to_apply", columnDefinition = "TEXT")
    private String howToApply;

    @Column(name = "skills_required")
    private String skillsRequired;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OpportunityStatus status;
}
