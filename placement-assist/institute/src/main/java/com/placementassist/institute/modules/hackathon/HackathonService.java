package com.placementassist.institute.modules.hackathon;

import com.placementassist.institute.common.enums.ListingType;
import com.placementassist.institute.common.enums.OpportunityMode;
import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.hackathon.dto.CreateHackathonRequest;
import com.placementassist.institute.modules.hackathon.dto.HackathonResponse;
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
 * Service managing external hackathon opportunities, filters, and student bookmarks.
 */
@Service
public class HackathonService {

    private final HackathonRepository hackathonRepository;
    private final ListingService listingService;
    private final HackathonMapper mapper;

    public HackathonService(
            HackathonRepository hackathonRepository,
            ListingService listingService,
            HackathonMapper mapper) {
        this.hackathonRepository = hackathonRepository;
        this.listingService = listingService;
        this.mapper = mapper;
    }

    @Transactional
    public HackathonResponse createHackathon(CreateHackathonRequest request) {
        Hackathon entity = mapper.toEntity(request);
        Hackathon saved = hackathonRepository.save(entity);
        return mapper.toResponse(saved, false);
    }

    @Transactional
    public HackathonResponse updateHackathon(Long id, CreateHackathonRequest request, Long studentId) {
        Hackathon hackathon = hackathonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hackathon", id));

        hackathon.setName(request.name());
        hackathon.setOrganizer(request.organizer());
        hackathon.setMode(request.mode());
        hackathon.setStartDate(request.startDate());
        hackathon.setEndDate(request.endDate());
        hackathon.setRegistrationDeadline(request.registrationDeadline());
        hackathon.setPrize(request.prize());
        hackathon.setTeamSize(request.teamSize());
        hackathon.setLocation(request.location());
        hackathon.setLink(request.link());
        hackathon.setDescription(request.description());
        hackathon.setHowToApply(request.howToApply());
        if (request.status() != null) {
            hackathon.setStatus(request.status());
        }

        Hackathon saved = hackathonRepository.save(hackathon);
        boolean bookmarked = listingService.isBookmarked(studentId, ListingType.HACKATHON, id);
        return mapper.toResponse(saved, bookmarked);
    }

    @Transactional(readOnly = true)
    public HackathonResponse getHackathonById(Long id, Long studentId) {
        Hackathon hackathon = hackathonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hackathon", id));
        boolean bookmarked = listingService.isBookmarked(studentId, ListingType.HACKATHON, id);
        return mapper.toResponse(hackathon, bookmarked);
    }

    @Transactional(readOnly = true)
    public PageResponse<HackathonResponse> listHackathons(
            String search,
            OpportunityMode mode,
            OpportunityStatus status,
            Long studentId,
            Pageable pageable) {

        SpecificationBuilder<Hackathon> builder = SpecificationBuilder.create();
        if (search != null && !search.isBlank()) {
            builder.withSearch(search, "name", "organizer");
        }
        if (mode != null) {
            builder.withEqual("mode", mode);
        }
        if (status != null) {
            builder.withEqual("status", status);
        }

        Page<Hackathon> page = hackathonRepository.findAll(builder.build(), pageable);
        List<HackathonResponse> content = page.getContent().stream()
                .map(h -> mapper.toResponse(h, listingService.isBookmarked(studentId, ListingType.HACKATHON, h.getId())))
                .toList();

        return PageResponse.<HackathonResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public List<HackathonResponse> getUpcomingHackathons(Long studentId) {
        List<Hackathon> list = hackathonRepository.findByStatusAndRegistrationDeadlineGreaterThanEqualOrderByRegistrationDeadlineAsc(
                OpportunityStatus.OPEN, LocalDate.now());
        return list.stream()
                .map(h -> mapper.toResponse(h, listingService.isBookmarked(studentId, ListingType.HACKATHON, h.getId())))
                .toList();
    }

    @Transactional
    public void deleteHackathon(Long id) {
        if (!hackathonRepository.existsById(id)) {
            throw new NotFoundException("Hackathon", id);
        }
        hackathonRepository.deleteById(id);
    }

    @Transactional
    public boolean toggleBookmark(Long hackathonId, Long studentId) {
        if (!hackathonRepository.existsById(hackathonId)) {
            throw new NotFoundException("Hackathon", hackathonId);
        }
        return listingService.toggleBookmark(studentId, ListingType.HACKATHON, hackathonId);
    }
}
