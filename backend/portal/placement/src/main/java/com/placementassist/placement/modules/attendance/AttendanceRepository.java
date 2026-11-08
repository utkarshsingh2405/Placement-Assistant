package com.placementassist.placement.modules.attendance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStageIdOrderByCheckInTimeDesc(Long stageId);

    Page<Attendance> findByStageIdOrderByCheckInTimeDesc(Long stageId, Pageable pageable);

    Optional<Attendance> findByStageIdAndRollNo(Long stageId, String rollNo);

    boolean existsByStageIdAndRollNo(Long stageId, String rollNo);

    long countByStageIdAndStatus(Long stageId, String status);

    long countByDriveIdAndStatus(Long driveId, String status);
}
