package com.placementassist.institute.modules.questionbank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {

    @Query(value = "SELECT * FROM questions " +
                   "WHERE (:difficulty IS NULL OR difficulty = :difficulty) " +
                   "AND (:role IS NULL OR LOWER(role) = LOWER(:role)) " +
                   "ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestions(
            @Param("difficulty") String difficulty,
            @Param("role") String role,
            @Param("limit") int limit);
}
