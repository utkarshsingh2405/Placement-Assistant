package com.placementassist.institute.modules.listing;

import com.placementassist.institute.common.enums.ListingType;
import com.placementassist.institute.modules.student.Student;
import com.placementassist.institute.modules.student.StudentRepository;
import com.placementassist.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Generic listing service providing unified "how-to-apply" steps parsing
 * and student bookmarking across Hackathons, Internships, and Off-Campus Jobs.
 */
@Service
public class ListingService {

    private final BookmarkRepository bookmarkRepository;
    private final StudentRepository studentRepository;

    public ListingService(BookmarkRepository bookmarkRepository, StudentRepository studentRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Parses free-form howToApply instructions into an ordered list of clean actionable steps.
     */
    public List<String> parseHowToApplySteps(String howToApply) {
        if (howToApply == null || howToApply.isBlank()) {
            return List.of("Review opportunity criteria and apply via external link.");
        }
        return Arrays.stream(howToApply.split("(\\r?\\n)+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.replaceFirst("^(\\d+[.)]|[-*•])\\s*", ""))
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /**
     * Toggles bookmark for a student on any listing.
     * Returns true if newly bookmarked, false if unbookmarked.
     */
    @Transactional
    public boolean toggleBookmark(Long studentId, ListingType listingType, Long listingId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student", studentId));

        Optional<Bookmark> existing = bookmarkRepository.findByStudentIdAndListingTypeAndListingId(studentId, listingType, listingId);
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return false;
        } else {
            Bookmark bookmark = Bookmark.builder()
                    .student(student)
                    .listingType(listingType)
                    .listingId(listingId)
                    .build();
            bookmarkRepository.save(bookmark);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public boolean isBookmarked(Long studentId, ListingType listingType, Long listingId) {
        if (studentId == null) return false;
        return bookmarkRepository.existsByStudentIdAndListingTypeAndListingId(studentId, listingType, listingId);
    }

    @Transactional(readOnly = true)
    public List<Long> getBookmarkedListingIds(Long studentId, ListingType listingType) {
        if (studentId == null) return List.of();
        return bookmarkRepository.findByStudentIdAndListingType(studentId, listingType).stream()
                .map(Bookmark::getListingId)
                .toList();
    }
}
