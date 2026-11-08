package com.placementassist.placement.modules.drive;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacementDriveRepository extends JpaRepository<PlacementDrive, Long> {

    List<PlacementDrive> findByStatus(String status);

    Page<PlacementDrive> findByTitleContainingIgnoreCaseOrJobRolesContainingIgnoreCase(String title, String jobRoles, Pageable pageable);

    long countByStatus(String status);
}
