package com.placementassist.institute.modules.offcampusjob;

import com.placementassist.institute.common.enums.OpportunityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OffCampusJobRepository extends JpaRepository<OffCampusJob, Long>, JpaSpecificationExecutor<OffCampusJob> {

    List<OffCampusJob> findByStatusAndDeadlineGreaterThanEqualOrderByDeadlineAsc(
            OpportunityStatus status, LocalDate date);

    List<OffCampusJob> findByDeadlineLessThanAndStatus(
            LocalDate date, OpportunityStatus status);

    List<OffCampusJob> findByDeadlineBetweenAndStatus(
            LocalDate from, LocalDate to, OpportunityStatus status);

    long countByStatus(OpportunityStatus status);
}
