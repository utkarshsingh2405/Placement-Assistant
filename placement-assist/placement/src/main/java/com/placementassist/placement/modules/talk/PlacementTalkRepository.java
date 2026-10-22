package com.placementassist.placement.modules.talk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlacementTalkRepository extends JpaRepository<PlacementTalk, Long> {

    Optional<PlacementTalk> findByDriveId(Long driveId);
}
