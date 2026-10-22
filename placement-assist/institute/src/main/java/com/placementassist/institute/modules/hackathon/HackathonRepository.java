package com.placementassist.institute.modules.hackathon;

import com.placementassist.institute.common.enums.OpportunityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long>, JpaSpecificationExecutor<Hackathon> {

    List<Hackathon> findByStatusAndRegistrationDeadlineGreaterThanEqualOrderByRegistrationDeadlineAsc(
            OpportunityStatus status, LocalDate date);

    List<Hackathon> findByRegistrationDeadlineLessThanAndStatus(
            LocalDate date, OpportunityStatus status);

    List<Hackathon> findByRegistrationDeadlineBetweenAndStatus(
            LocalDate from, LocalDate to, OpportunityStatus status);

    long countByStatus(OpportunityStatus status);
}
