package com.placementassist.placement.modules.stage;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "placement_stages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacementStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "drive_id", nullable = false)
    private Long driveId;

    @Column(name = "stage_name", nullable = false)
    private String stageName;

    @Column(name = "stage_order", nullable = false)
    private Integer stageOrder;

    @Column(name = "stage_type", nullable = false)
    private String stageType; // REGISTRATION, PPT, ONLINE_TEST, TECHNICAL_INTERVIEW_1, TECHNICAL_INTERVIEW_2, HR_INTERVIEW, OFFER

    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING"; // PENDING, ACTIVE, COMPLETED

    @Column(name = "venue_or_link")
    private String venueOrLink;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
