package com.placementassist.institute.modules.listing;

import com.placementassist.institute.common.BaseEntity;
import com.placementassist.institute.common.enums.ListingType;
import com.placementassist.institute.modules.student.Student;
import jakarta.persistence.*;
import lombok.*;

/**
 * Student saved bookmark for an external opportunity listing
 * (Hackathon, Internship, or Off-Campus Job).
 */
@Entity
@Table(name = "bookmarks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "listing_type", "listing_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_type", nullable = false)
    private ListingType listingType;

    @Column(name = "listing_id", nullable = false)
    private Long listingId;
}
