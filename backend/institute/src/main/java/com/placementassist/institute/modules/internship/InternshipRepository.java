package com.placementassist.institute.modules.internship;

import com.placementassist.institute.common.enums.OpportunityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long>, JpaSpecificationExecutor<Internship> {

    List<Internship> findByStatusAndDeadlineGreaterThanEqualOrderByDeadlineAsc(
            OpportunityStatus status, LocalDate date);

    List<Internship> findByDeadlineLessThanAndStatus(
            LocalDate date, OpportunityStatus status);

    List<Internship> findByDeadlineBetweenAndStatus(
            LocalDate from, LocalDate to, OpportunityStatus status);

    long countByStatus(OpportunityStatus status);
}
