package com.placementassist.institute.modules.attendance.crt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CrtSessionRepository extends JpaRepository<CrtSession, Long>, JpaSpecificationExecutor<CrtSession> {

    List<CrtSession> findBySessionDateBetweenOrderBySessionDateAscStartTimeAsc(LocalDate from, LocalDate to);

    List<CrtSession> findAllByOrderBySessionDateDescStartTimeDesc();
}
