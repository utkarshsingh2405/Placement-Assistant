package com.placementassist.placement.modules.result;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlacementResultRepository extends JpaRepository<PlacementResult, Long> {

    List<PlacementResult> findByStageIdOrderByRollNoAsc(Long stageId);

    List<PlacementResult> findByStageIdAndResultStatus(Long stageId, String resultStatus);

    List<PlacementResult> findByDriveIdAndRollNo(Long driveId, String rollNo);

    Optional<PlacementResult> findByStageIdAndRollNo(Long stageId, String rollNo);

    boolean existsByStageIdAndRollNo(Long stageId, String rollNo);

    long countByStageIdAndResultStatus(Long stageId, String resultStatus);
}
