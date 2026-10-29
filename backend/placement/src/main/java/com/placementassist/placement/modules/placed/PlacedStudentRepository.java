package com.placementassist.placement.modules.placed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlacedStudentRepository extends JpaRepository<PlacedStudent, Long> {

    List<PlacedStudent> findAllByOrderByPackageLpaDesc();

    Page<PlacedStudent> findAllByOrderByPackageLpaDesc(Pageable pageable);

    Optional<PlacedStudent> findByRollNo(String rollNo);

    boolean existsByRollNo(String rollNo);

    @Query("SELECT MAX(p.packageLpa) FROM PlacedStudent p")
    Double findMaxPackage();

    @Query("SELECT AVG(p.packageLpa) FROM PlacedStudent p")
    Double findAvgPackage();

    @Query("SELECT COUNT(DISTINCT p.companyName) FROM PlacedStudent p")
    Long countDistinctCompanies();
}
