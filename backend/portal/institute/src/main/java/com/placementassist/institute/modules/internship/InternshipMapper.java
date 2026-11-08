package com.placementassist.institute.modules.internship;

import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.internship.dto.CreateInternshipRequest;
import com.placementassist.institute.modules.internship.dto.InternshipResponse;
import com.placementassist.institute.modules.listing.ListingService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InternshipMapper {

    private final ListingService listingService;

    public InternshipMapper(ListingService listingService) {
        this.listingService = listingService;
    }

    public Internship toEntity(CreateInternshipRequest request) {
        return Internship.builder()
                .company(request.company())
                .role(request.role())
                .stipend(request.stipend())
                .durationMonths(request.durationMonths())
                .location(request.location())
                .mode(request.mode())
                .eligibility(request.eligibility())
                .deadline(request.deadline())
                .applyUrl(request.applyUrl())
                .howToApply(request.howToApply())
                .skillsRequired(request.skillsRequired())
                .status(request.status() != null ? request.status() : OpportunityStatus.OPEN)
                .build();
    }

    public InternshipResponse toResponse(Internship internship, boolean bookmarked) {
        List<String> steps = listingService.parseHowToApplySteps(internship.getHowToApply());

        return new InternshipResponse(
                internship.getId(),
                internship.getCompany(),
                internship.getRole(),
                internship.getStipend(),
                internship.getDurationMonths(),
                internship.getLocation(),
                internship.getMode(),
                internship.getEligibility(),
                internship.getDeadline(),
                internship.getApplyUrl(),
                internship.getHowToApply(),
                steps,
                internship.getSkillsRequired(),
                internship.getStatus(),
                bookmarked,
                internship.getCreatedAt(),
                internship.getUpdatedAt()
        );
    }
}
