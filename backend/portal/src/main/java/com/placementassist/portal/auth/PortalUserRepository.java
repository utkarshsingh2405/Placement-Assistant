package com.placementassist.portal.auth;

import com.placementassist.shared.security.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Portal users.
 */
@Repository
public interface PortalUserRepository extends JpaRepository<PortalUser, Long> {

    Optional<PortalUser> findByEmail(String email);

    Optional<PortalUser> findByRollNo(String rollNo);

    Optional<PortalUser> findByResetToken(String resetToken);

    boolean existsByEmail(String email);

    boolean existsByRollNo(String rollNo);

    @Query("SELECT u FROM PortalUser u WHERE " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:search IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.rollNo) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<PortalUser> searchUsers(@Param("role") Role role, @Param("search") String search, Pageable pageable);
}
