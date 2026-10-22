package com.placementassist.institute.modules.attendance.classroom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long>, JpaSpecificationExecutor<ClassSession> {

    List<ClassSession> findByBranchAndYearAndSectionOrderBySessionDateDescStartTimeDesc(
            String branch, Integer year, String section);

    List<ClassSession> findByBranchAndYearOrderBySessionDateDescStartTimeDesc(
            String branch, Integer year);

    List<ClassSession> findBySessionDateBetweenOrderBySessionDateAscStartTimeAsc(
            LocalDate from, LocalDate to);

    List<ClassSession> findAllByOrderBySessionDateDescStartTimeDesc();
}
