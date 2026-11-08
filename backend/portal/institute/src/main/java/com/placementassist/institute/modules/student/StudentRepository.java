package com.placementassist.institute.modules.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    Optional<Student> findByEmail(String email);

    Optional<Student> findByRollNo(String rollNo);

    Optional<Student> findByUserId(Long userId);

    boolean existsByEmail(String email);

    boolean existsByRollNo(String rollNo);

    long countByPlacementEligibleTrue();
}
