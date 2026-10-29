package com.placementassist.placement.modules.liveupdate;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "placement_live_updates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "drive_id", nullable = false)
    private Long driveId;

    @Column(name = "stage_id")
    private Long stageId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(nullable = false)
    @Builder.Default
    private String category = "ANNOUNCEMENT"; // ANNOUNCEMENT, SHORTLIST, SCHEDULE_CHANGE, STAGE_STARTED, OFFERS_RELEASED

    @Column(nullable = false)
    @Builder.Default
    private String priority = "INFO"; // INFO, HIGH, URGENT

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
