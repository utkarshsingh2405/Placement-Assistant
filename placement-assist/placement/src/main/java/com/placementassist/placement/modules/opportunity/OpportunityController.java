package com.placementassist.placement.modules.opportunity;

import com.placementassist.placement.integration.dto.OpportunityDto;
import com.placementassist.shared.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OpportunityDto>>> getAllOpportunities(
            @RequestParam(required = false) String type) {
        List<OpportunityDto> list;
        if ("INTERNSHIP".equalsIgnoreCase(type)) {
            list = opportunityService.getInternships();
        } else if ("HACKATHON".equalsIgnoreCase(type)) {
            list = opportunityService.getHackathons();
        } else if ("OFFCAMPUS_JOB".equalsIgnoreCase(type)) {
            list = opportunityService.getOffCampusJobs();
        } else {
            list = opportunityService.getAllOpportunities();
        }
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/internships")
    public ResponseEntity<ApiResponse<List<OpportunityDto>>> getInternships() {
        return ResponseEntity.ok(ApiResponse.ok(opportunityService.getInternships()));
    }

    @GetMapping("/hackathons")
    public ResponseEntity<ApiResponse<List<OpportunityDto>>> getHackathons() {
        return ResponseEntity.ok(ApiResponse.ok(opportunityService.getHackathons()));
    }

    @GetMapping("/offcampus")
    public ResponseEntity<ApiResponse<List<OpportunityDto>>> getOffCampusJobs() {
        return ResponseEntity.ok(ApiResponse.ok(opportunityService.getOffCampusJobs()));
    }
}
