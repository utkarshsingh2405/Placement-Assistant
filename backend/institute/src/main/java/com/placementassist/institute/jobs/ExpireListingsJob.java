package com.placementassist.institute.jobs;

import com.placementassist.institute.common.enums.OpportunityStatus;
import com.placementassist.institute.modules.hackathon.Hackathon;
import com.placementassist.institute.modules.hackathon.HackathonRepository;
import com.placementassist.institute.modules.internship.Internship;
import com.placementassist.institute.modules.internship.InternshipRepository;
import com.placementassist.institute.modules.offcampusjob.OffCampusJob;
import com.placementassist.institute.modules.offcampusjob.OffCampusJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Nightly scheduled job that automatically marks past-deadline
 * hackathons, internships, and off-campus jobs as CLOSED.
 */
@Component
@ConditionalOnBean(HackathonRepository.class)
public class ExpireListingsJob {

    private static final Logger log = LoggerFactory.getLogger(ExpireListingsJob.class);

    private final HackathonRepository hackathonRepository;
    private final InternshipRepository internshipRepository;
    private final OffCampusJobRepository jobRepository;

    public ExpireListingsJob(
            HackathonRepository hackathonRepository,
            InternshipRepository internshipRepository,
            OffCampusJobRepository jobRepository) {
        this.hackathonRepository = hackathonRepository;
        this.internshipRepository = internshipRepository;
        this.jobRepository = jobRepository;
    }

    @Scheduled(cron = "${jobs.expire-listings.cron:0 0 1 * * ?}")
    @Transactional
    public void expirePastDeadlineListings() {
        LocalDate today = LocalDate.now();
        log.info("Running ExpireListingsJob for past deadlines before {}", today);

        // 1. Expire Hackathons
        List<Hackathon> expiredHackathons = hackathonRepository.findByRegistrationDeadlineLessThanAndStatus(today, OpportunityStatus.OPEN);
        for (Hackathon h : expiredHackathons) {
            h.setStatus(OpportunityStatus.CLOSED);
        }
        hackathonRepository.saveAll(expiredHackathons);

        // 2. Expire Internships
        List<Internship> expiredInternships = internshipRepository.findByDeadlineLessThanAndStatus(today, OpportunityStatus.OPEN);
        for (Internship i : expiredInternships) {
            i.setStatus(OpportunityStatus.CLOSED);
        }
        internshipRepository.saveAll(expiredInternships);

        // 3. Expire Off-Campus Jobs
        List<OffCampusJob> expiredJobs = jobRepository.findByDeadlineLessThanAndStatus(today, OpportunityStatus.OPEN);
        for (OffCampusJob j : expiredJobs) {
            j.setStatus(OpportunityStatus.CLOSED);
        }
        jobRepository.saveAll(expiredJobs);

        int totalExpired = expiredHackathons.size() + expiredInternships.size() + expiredJobs.size();
        log.info("ExpireListingsJob completed. Marked {} listings as CLOSED (Hackathons: {}, Internships: {}, Jobs: {}).",
                totalExpired, expiredHackathons.size(), expiredInternships.size(), expiredJobs.size());
    }
}
