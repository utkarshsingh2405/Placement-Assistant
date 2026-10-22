package com.placementassist.institute.modules.student;

import com.placementassist.institute.common.BaseEntity;
import com.placementassist.institute.modules.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Student profile entity.
 */
@Entity
@Table(name = "students")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "roll_no", unique = true, nullable = false)
    private String rollNo;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "branch", nullable = false)
    private String branch;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "cgpa", nullable = false)
    private Double cgpa;

    @Column(name = "backlogs", nullable = false)
    private Integer backlogs;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_skills", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "skill")
    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @Column(name = "linkedin")
    private String linkedin;

    @Column(name = "github")
    private String github;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "placement_eligible", nullable = false)
    @Builder.Default
    private Boolean placementEligible = false;
}
