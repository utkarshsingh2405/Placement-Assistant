package com.placementassist.institute.modules.attendance.crt;

import com.placementassist.institute.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Campus Recruitment Training (CRT) session entity.
 */
@Entity
@Table(name = "crt_sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrtSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "trainer", nullable = false)
    private String trainer;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "start_time", nullable = false)
    private String startTime; // e.g. "09:30"

    @Column(name = "end_time", nullable = false)
    private String endTime; // e.g. "12:30"

    @Column(name = "venue")
    private String venue;
}
