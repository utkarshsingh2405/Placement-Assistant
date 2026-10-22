package com.placementassist.institute.modules.listing;

import com.placementassist.institute.common.enums.ListingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByStudentId(Long studentId);

    List<Bookmark> findByStudentIdAndListingType(Long studentId, ListingType listingType);

    Optional<Bookmark> findByStudentIdAndListingTypeAndListingId(Long studentId, ListingType listingType, Long listingId);

    boolean existsByStudentIdAndListingTypeAndListingId(Long studentId, ListingType listingType, Long listingId);

    void deleteByStudentIdAndListingTypeAndListingId(Long studentId, ListingType listingType, Long listingId);
}
