package com.placementassist.placement.modules.stage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlacementStageRepository extends JpaRepository<PlacementStage, Long> {

    List<PlacementStage> findByDriveIdOrderByStageOrderAsc(Long driveId);

    Optional<PlacementStage> findByDriveIdAndStageOrder(Long driveId, Integer stageOrder);

    Optional<PlacementStage> findByDriveIdAndStatus(Long driveId, String status);
}
