package com.placementassist.placement.modules.talk;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "placement_talks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacementTalk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "drive_id", nullable = false, unique = true)
    private Long driveId;

    @Column(name = "scheduled_at", nullable = false)
    private String scheduledAt;

    @Column(name = "venue_or_url")
    private String venueOrUrl;

    @Column(name = "presentation_url")
    private String presentationUrl;

    @Column(name = "recording_url")
    private String recordingUrl;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(name = "speaker_name")
    private String speakerName;

    @Column(name = "speaker_title")
    private String speakerTitle;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
