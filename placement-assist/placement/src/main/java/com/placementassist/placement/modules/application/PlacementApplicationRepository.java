package com.placementassist.placement.modules.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlacementApplicationRepository extends JpaRepository<PlacementApplicationEntity, Long> {

    List<PlacementApplicationEntity> findByDriveId(Long driveId);

    Page<PlacementApplicationEntity> findByDriveId(Long driveId, Pageable pageable);

    Page<PlacementApplicationEntity> findByDriveIdAndStatus(Long driveId, String status, Pageable pageable);

    List<PlacementApplicationEntity> findByRollNo(String rollNo);

    Optional<PlacementApplicationEntity> findByDriveIdAndRollNo(Long driveId, String rollNo);

    boolean existsByDriveIdAndRollNo(Long driveId, String rollNo);

    long countByDriveId(Long driveId);

    long countByDriveIdAndStatus(Long driveId, String status);
}
