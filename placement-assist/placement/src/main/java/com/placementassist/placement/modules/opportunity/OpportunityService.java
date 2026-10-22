package com.placementassist.placement.modules.opportunity;

import com.placementassist.placement.integration.InstituteClient;
import com.placementassist.placement.integration.dto.OpportunityDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpportunityService {

    private final InstituteClient instituteClient;

    public OpportunityService(InstituteClient instituteClient) {
        this.instituteClient = instituteClient;
    }

    public List<OpportunityDto> getAllOpportunities() {
        List<OpportunityDto> all = new ArrayList<>();
        all.addAll(instituteClient.getInternships());
        all.addAll(instituteClient.getHackathons());
        all.addAll(instituteClient.getOffCampusJobs());
        return all;
    }

    public List<OpportunityDto> getInternships() {
        return instituteClient.getInternships();
    }

    public List<OpportunityDto> getHackathons() {
        return instituteClient.getHackathons();
    }

    public List<OpportunityDto> getOffCampusJobs() {
        return instituteClient.getOffCampusJobs();
    }
}
