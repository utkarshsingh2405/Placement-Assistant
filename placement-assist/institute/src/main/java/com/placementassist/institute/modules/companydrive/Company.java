package com.placementassist.institute.modules.companydrive;

import com.placementassist.institute.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Recruiting company entity.
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "website")
    private String website;

    @Column(name = "about", length = 2000)
    private String about;

    @Column(name = "industry")
    private String industry;

    @Column(name = "headquarters")
    private String headquarters;

    @Column(name = "logo_url")
    private String logoUrl;
}
