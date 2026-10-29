package com.placementassist.institute.modules.internship;

import com.placementassist.institute.common.enums.ListingType;
import com.placementassist.institute.common.enums.OpportunityMode;
import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.internship.dto.CreateInternshipRequest;
import com.placementassist.institute.modules.internship.dto.InternshipResponse;
import com.placementassist.institute.modules.listing.ListingService;
import com.placementassist.shared.api.PageResponse;
import com.placementassist.shared.exception.NotFoundException;
import com.placementassist.shared.util.SpecificationBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service managing corporate and research internship listings, filters, and student bookmarks.
 */
@Service
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final ListingService listingService;
    private final InternshipMapper mapper;

    public InternshipService(
            InternshipRepository internshipRepository,
            ListingService listingService,
            InternshipMapper mapper) {
        this.internshipRepository = internshipRepository;
        this.listingService = listingService;
        this.mapper = mapper;
    }

    @Transactional
    public InternshipResponse createInternship(CreateInternshipRequest request) {
        Internship entity = mapper.toEntity(request);
        Internship saved = internshipRepository.save(entity);
        return mapper.toResponse(saved, false);
    }

    @Transactional
    public InternshipResponse updateInternship(Long id, CreateInternshipRequest request, Long studentId) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Internship", id));

        internship.setCompany(request.company());
        internship.setRole(request.role());
        internship.setStipend(request.stipend());
        internship.setDurationMonths(request.durationMonths());
        internship.setLocation(request.location());
        internship.setMode(request.mode());
        internship.setEligibility(request.eligibility());
        internship.setDeadline(request.deadline());
        internship.setApplyUrl(request.applyUrl());
        internship.setHowToApply(request.howToApply());
        internship.setSkillsRequired(request.skillsRequired());
        if (request.status() != null) {
            internship.setStatus(request.status());
        }

        Internship saved = internshipRepository.save(internship);
        boolean bookmarked = listingService.isBookmarked(studentId, ListingType.INTERNSHIP, id);
        return mapper.toResponse(saved, bookmarked);
    }

    @Transactional(readOnly = true)
    public InternshipResponse getInternshipById(Long id, Long studentId) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Internship", id));
        boolean bookmarked = listingService.isBookmarked(studentId, ListingType.INTERNSHIP, id);
        return mapper.toResponse(internship, bookmarked);
    }

    @Transactional(readOnly = true)
    public PageResponse<InternshipResponse> listInternships(
            String search,
            OpportunityMode mode,
            OpportunityStatus status,
            Long studentId,
            Pageable pageable) {

        SpecificationBuilder<Internship> builder = SpecificationBuilder.create();
        if (search != null && !search.isBlank()) {
            builder.withSearch(search, "company", "role", "skillsRequired");
        }
        if (mode != null) {
            builder.withEqual("mode", mode);
        }
        if (status != null) {
            builder.withEqual("status", status);
        }

        Page<Internship> page = internshipRepository.findAll(builder.build(), pageable);
        List<InternshipResponse> content = page.getContent().stream()
                .map(i -> mapper.toResponse(i, listingService.isBookmarked(studentId, ListingType.INTERNSHIP, i.getId())))
                .toList();

        return PageResponse.<InternshipResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public List<InternshipResponse> getUpcomingInternships(Long studentId) {
        List<Internship> list = internshipRepository.findByStatusAndDeadlineGreaterThanEqualOrderByDeadlineAsc(
                OpportunityStatus.OPEN, LocalDate.now());
        return list.stream()
                .map(i -> mapper.toResponse(i, listingService.isBookmarked(studentId, ListingType.INTERNSHIP, i.getId())))
                .toList();
    }

    @Transactional
    public void deleteInternship(Long id) {
        if (!internshipRepository.existsById(id)) {
            throw new NotFoundException("Internship", id);
        }
        internshipRepository.deleteById(id);
    }

    @Transactional
    public boolean toggleBookmark(Long internshipId, Long studentId) {
        if (!internshipRepository.existsById(internshipId)) {
            throw new NotFoundException("Internship", internshipId);
        }
        return listingService.toggleBookmark(studentId, ListingType.INTERNSHIP, internshipId);
    }

    @Transactional(readOnly = true)
    public List<InternshipResponse> getBookmarkedInternships(Long studentId) {
        if (studentId == null) return List.of();
        List<Long> ids = listingService.getBookmarkedListingIds(studentId, ListingType.INTERNSHIP);
        if (ids.isEmpty()) return List.of();
        return internshipRepository.findAllById(ids).stream()
                .map(i -> mapper.toResponse(i, true))
                .toList();
    }
}

