package com.placementassist.institute.modules.hackathon;

import com.placementassist.institute.common.BaseEntity;
import com.placementassist.institute.common.enums.OpportunityMode;
import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.listing.ListingItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "hackathons")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hackathon extends BaseEntity implements ListingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "organizer", nullable = false)
    private String organizer;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private OpportunityMode mode;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "registration_deadline", nullable = false)
    private LocalDate registrationDeadline;

    @Column(name = "prize")
    private String prize;

    @Column(name = "team_size")
    private Integer teamSize;

    @Column(name = "location")
    private String location;

    @Column(name = "link")
    private String link;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "how_to_apply", columnDefinition = "TEXT")
    private String howToApply;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OpportunityStatus status;

    @Override
    public LocalDate getDeadline() {
        return registrationDeadline;
    }
}
