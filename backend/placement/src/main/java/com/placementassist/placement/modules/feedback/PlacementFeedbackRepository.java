package com.placementassist.placement.modules.feedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacementFeedbackRepository extends JpaRepository<PlacementFeedback, Long> {

    List<PlacementFeedback> findByDriveIdOrderByCreatedAtDesc(Long driveId);

    List<PlacementFeedback> findAllByOrderByCreatedAtDesc();

    @Query("SELECT AVG(f.rating) FROM PlacementFeedback f WHERE f.driveId = :driveId")
    Double getAverageRatingForDrive(Long driveId);
}
