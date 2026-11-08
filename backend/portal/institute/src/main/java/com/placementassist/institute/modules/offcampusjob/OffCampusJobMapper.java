package com.placementassist.institute.modules.offcampusjob;

import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.listing.ListingService;
import com.placementassist.institute.modules.offcampusjob.dto.CreateOffCampusJobRequest;
import com.placementassist.institute.modules.offcampusjob.dto.OffCampusJobResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OffCampusJobMapper {

    private final ListingService listingService;

    public OffCampusJobMapper(ListingService listingService) {
        this.listingService = listingService;
    }

    public OffCampusJob toEntity(CreateOffCampusJobRequest request) {
        return OffCampusJob.builder()
                .company(request.company())
                .role(request.role())
                .packageLpa(request.packageLpa())
                .location(request.location())
                .batchEligible(request.batchEligible())
                .experience(request.experience())
                .skillsRequired(request.skillsRequired())
                .deadline(request.deadline())
                .applyUrl(request.applyUrl())
                .howToApply(request.howToApply())
                .status(request.status() != null ? request.status() : OpportunityStatus.OPEN)
                .build();
    }

    public OffCampusJobResponse toResponse(OffCampusJob job, boolean bookmarked) {
        List<String> steps = listingService.parseHowToApplySteps(job.getHowToApply());

        return new OffCampusJobResponse(
                job.getId(),
                job.getCompany(),
                job.getRole(),
                job.getPackageLpa(),
                job.getLocation(),
                job.getBatchEligible(),
                job.getExperience(),
                job.getSkillsRequired(),
                job.getDeadline(),
                job.getApplyUrl(),
                job.getHowToApply(),
                steps,
                job.getStatus(),
                bookmarked,
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }
}
