package com.placementassist.institute.modules.offcampusjob;

import com.placementassist.institute.common.enums.ListingType;
import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.listing.ListingService;
import com.placementassist.institute.modules.offcampusjob.dto.CreateOffCampusJobRequest;
import com.placementassist.institute.modules.offcampusjob.dto.OffCampusJobResponse;
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
 * Service managing off-campus job recruitment drives, package metrics, filters, and bookmarks.
 */
@Service
public class OffCampusJobService {

    private final OffCampusJobRepository jobRepository;
    private final ListingService listingService;
    private final OffCampusJobMapper mapper;

    public OffCampusJobService(
            OffCampusJobRepository jobRepository,
            ListingService listingService,
            OffCampusJobMapper mapper) {
        this.jobRepository = jobRepository;
        this.listingService = listingService;
        this.mapper = mapper;
    }

    @Transactional
    public OffCampusJobResponse createJob(CreateOffCampusJobRequest request) {
        OffCampusJob entity = mapper.toEntity(request);
        OffCampusJob saved = jobRepository.save(entity);
        return mapper.toResponse(saved, false);
    }

    @Transactional
    public OffCampusJobResponse updateJob(Long id, CreateOffCampusJobRequest request, Long studentId) {
        OffCampusJob job = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("OffCampusJob", id));

        job.setCompany(request.company());
        job.setRole(request.role());
        job.setPackageLpa(request.packageLpa());
        job.setLocation(request.location());
        job.setBatchEligible(request.batchEligible());
        job.setExperience(request.experience());
        job.setSkillsRequired(request.skillsRequired());
        job.setDeadline(request.deadline());
        job.setApplyUrl(request.applyUrl());
        job.setHowToApply(request.howToApply());
        if (request.status() != null) {
            job.setStatus(request.status());
        }

        OffCampusJob saved = jobRepository.save(job);
        boolean bookmarked = listingService.isBookmarked(studentId, ListingType.JOB, id);
        return mapper.toResponse(saved, bookmarked);
    }

    @Transactional(readOnly = true)
    public OffCampusJobResponse getJobById(Long id, Long studentId) {
        OffCampusJob job = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("OffCampusJob", id));
        boolean bookmarked = listingService.isBookmarked(studentId, ListingType.JOB, id);
        return mapper.toResponse(job, bookmarked);
    }

    @Transactional(readOnly = true)
    public PageResponse<OffCampusJobResponse> listJobs(
            String search,
            OpportunityStatus status,
            Double minPackage,
            Long studentId,
            Pageable pageable) {

        SpecificationBuilder<OffCampusJob> builder = SpecificationBuilder.create();
        if (search != null && !search.isBlank()) {
            builder.withSearch(search, "company", "role", "skillsRequired", "location");
        }
        if (status != null) {
            builder.withEqual("status", status);
        }
        if (minPackage != null) {
            builder.withGreaterThanOrEqualTo("packageLpa", minPackage);
        }

        Page<OffCampusJob> page = jobRepository.findAll(builder.build(), pageable);
        List<OffCampusJobResponse> content = page.getContent().stream()
                .map(j -> mapper.toResponse(j, listingService.isBookmarked(studentId, ListingType.JOB, j.getId())))
                .toList();

        return PageResponse.<OffCampusJobResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public List<OffCampusJobResponse> getUpcomingJobs(Long studentId) {
        List<OffCampusJob> list = jobRepository.findByStatusAndDeadlineGreaterThanEqualOrderByDeadlineAsc(
                OpportunityStatus.OPEN, LocalDate.now());
        return list.stream()
                .map(j -> mapper.toResponse(j, listingService.isBookmarked(studentId, ListingType.JOB, j.getId())))
                .toList();
    }

    @Transactional
    public void deleteJob(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new NotFoundException("OffCampusJob", id);
        }
        jobRepository.deleteById(id);
    }

    @Transactional
    public boolean toggleBookmark(Long jobId, Long studentId) {
        if (!jobRepository.existsById(jobId)) {
            throw new NotFoundException("OffCampusJob", jobId);
        }
        return listingService.toggleBookmark(studentId, ListingType.JOB, jobId);
    }

    @Transactional(readOnly = true)
    public List<OffCampusJobResponse> getBookmarkedJobs(Long studentId) {
        if (studentId == null) return List.of();
        List<Long> ids = listingService.getBookmarkedListingIds(studentId, ListingType.JOB);
        if (ids.isEmpty()) return List.of();
        return jobRepository.findAllById(ids).stream()
                .map(j -> mapper.toResponse(j, true))
                .toList();
    }
}

