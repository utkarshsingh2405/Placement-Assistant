package com.placementassist.placement.modules.liveupdate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveUpdateRepository extends JpaRepository<LiveUpdate, Long> {

    List<LiveUpdate> findByDriveIdOrderByCreatedAtDesc(Long driveId);

    Page<LiveUpdate> findByDriveIdOrderByCreatedAtDesc(Long driveId, Pageable pageable);

    List<LiveUpdate> findTop20ByOrderByCreatedAtDesc();
}
