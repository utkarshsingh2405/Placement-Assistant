package com.placementassist.institute.modules.companydrive;

import com.placementassist.institute.common.enums.DriveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DriveRepository extends JpaRepository<Drive, Long>, JpaSpecificationExecutor<Drive> {

    List<Drive> findByStatusOrderByDriveDateAsc(DriveStatus status);

    List<Drive> findByStatusAndLastDateToApplyGreaterThanEqualOrderByDriveDateAsc(DriveStatus status, LocalDate date);

    List<Drive> findByDriveDateGreaterThanEqualOrderByDriveDateAsc(LocalDate date);

    long countByStatus(DriveStatus status);
}
