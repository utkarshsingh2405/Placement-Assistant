package com.placementassist.institute.modules.driveround;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriveRoundRepository extends JpaRepository<DriveRound, Long> {

    List<DriveRound> findByDriveIdOrderByOrderNoAsc(Long driveId);

    long countByDriveId(Long driveId);
}
