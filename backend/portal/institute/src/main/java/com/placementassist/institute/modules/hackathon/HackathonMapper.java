package com.placementassist.institute.modules.hackathon;

import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.hackathon.dto.CreateHackathonRequest;
import com.placementassist.institute.modules.hackathon.dto.HackathonResponse;
import com.placementassist.institute.modules.listing.ListingService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HackathonMapper {

    private final ListingService listingService;

    public HackathonMapper(ListingService listingService) {
        this.listingService = listingService;
    }

    public Hackathon toEntity(CreateHackathonRequest request) {
        return Hackathon.builder()
                .name(request.name())
                .organizer(request.organizer())
                .mode(request.mode())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .registrationDeadline(request.registrationDeadline())
                .prize(request.prize())
                .teamSize(request.teamSize())
                .location(request.location())
                .link(request.link())
                .description(request.description())
                .howToApply(request.howToApply())
                .status(request.status() != null ? request.status() : OpportunityStatus.OPEN)
                .build();
    }

    public HackathonResponse toResponse(Hackathon hackathon, boolean bookmarked) {
        List<String> steps = listingService.parseHowToApplySteps(hackathon.getHowToApply());

        return new HackathonResponse(
                hackathon.getId(),
                hackathon.getName(),
                hackathon.getOrganizer(),
                hackathon.getMode(),
                hackathon.getStartDate(),
                hackathon.getEndDate(),
                hackathon.getRegistrationDeadline(),
                hackathon.getPrize(),
                hackathon.getTeamSize(),
                hackathon.getLocation(),
                hackathon.getLink(),
                hackathon.getDescription(),
                hackathon.getHowToApply(),
                steps,
                hackathon.getStatus(),
                bookmarked,
                hackathon.getCreatedAt(),
                hackathon.getUpdatedAt()
        );
    }
}
