package com.placementassist.institute.modules.driveround;

import com.placementassist.institute.common.BaseEntity;
import com.placementassist.institute.common.enums.RoundType;
import com.placementassist.institute.modules.companydrive.Drive;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Individual evaluation round in a recruitment drive.
 */
@Entity
@Table(name = "drive_rounds")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriveRound extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drive_id", nullable = false)
    private Drive drive;

    @Enumerated(EnumType.STRING)
    @Column(name = "round_type", nullable = false)
    private RoundType roundType;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(name = "round_date")
    private LocalDate roundDate;

    @Column(name = "venue")
    private String venue;

    @Column(name = "duration_mins")
    private Integer durationMins;

    @Column(name = "cutoff")
    private Double cutoff;

    @Column(name = "instructions", length = 2000)
    private String instructions;
}
