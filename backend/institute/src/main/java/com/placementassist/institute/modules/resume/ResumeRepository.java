package com.placementassist.institute.modules.resume;

import com.placementassist.institute.common.enums.ResumeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long>, JpaSpecificationExecutor<Resume> {

    List<Resume> findByStudentIdOrderByVersionDesc(Long studentId);

    Optional<Resume> findTopByStudentIdOrderByVersionDesc(Long studentId);

    Optional<Resume> findTopByStudentIdAndStatusOrderByVersionDesc(Long studentId, ResumeStatus status);

    Page<Resume> findByStatus(ResumeStatus status, Pageable pageable);

    Page<Resume> findByStudentId(Long studentId, Pageable pageable);

    long countByStatus(ResumeStatus status);
}
