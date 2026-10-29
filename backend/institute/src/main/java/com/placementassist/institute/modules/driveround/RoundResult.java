package com.placementassist.institute.modules.driveround;

import com.placementassist.institute.common.BaseEntity;
import com.placementassist.institute.common.enums.RoundResultStatus;
import com.placementassist.institute.modules.student.Student;
import jakarta.persistence.*;
import lombok.*;

/**
 * Result record of a student in a specific drive evaluation round.
 */
@Entity
@Table(name = "round_results", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"round_id", "student_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoundResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private DriveRound round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "score")
    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false)
    @Builder.Default
    private RoundResultStatus result = RoundResultStatus.PENDING;

    @Column(name = "remarks")
    private String remarks;
}
