package com.placementassist.institute.modules.driveround;

import com.placementassist.institute.common.enums.RoundResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoundResultRepository extends JpaRepository<RoundResult, Long> {

    List<RoundResult> findByRoundId(Long roundId);

    Optional<RoundResult> findByRoundIdAndStudentId(Long roundId, Long studentId);

    long countByRoundIdAndResult(Long roundId, RoundResultStatus result);

    @Query("SELECT r.round.orderNo, r.round.roundType, COUNT(r) FROM RoundResult r " +
           "WHERE r.round.drive.id = :driveId AND r.result = 'PASSED' " +
           "GROUP BY r.round.orderNo, r.round.roundType ORDER BY r.round.orderNo ASC")
    List<Object[]> getDriveFunnelStats(@Param("driveId") Long driveId);
}
