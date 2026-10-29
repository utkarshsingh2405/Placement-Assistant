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
 * Daily scheduled job that detects opportunities closing within 3 days
 * and generates system logs and deadline reminders.
 */
@Component
@ConditionalOnBean(HackathonRepository.class)
public class RemindersJob {

    private static final Logger log = LoggerFactory.getLogger(RemindersJob.class);

    private final HackathonRepository hackathonRepository;
    private final InternshipRepository internshipRepository;
    private final OffCampusJobRepository jobRepository;

    public RemindersJob(
            HackathonRepository hackathonRepository,
            InternshipRepository internshipRepository,
            OffCampusJobRepository jobRepository) {
        this.hackathonRepository = hackathonRepository;
        this.internshipRepository = internshipRepository;
        this.jobRepository = jobRepository;
    }

    @Scheduled(cron = "${jobs.reminders.cron:0 0 8 * * ?}")
    @Transactional(readOnly = true)
    public void flagClosingSoonListings() {
        LocalDate today = LocalDate.now();
        LocalDate windowEnd = today.plusDays(3);

        log.info("Running RemindersJob for listings closing between {} and {}", today, windowEnd);

        List<Hackathon> closingHackathons = hackathonRepository.findByRegistrationDeadlineBetweenAndStatus(
                today, windowEnd, OpportunityStatus.OPEN);
        for (Hackathon h : closingHackathons) {
            log.info("REMINDER: Hackathon '{}' deadline is approaching on {}", h.getName(), h.getRegistrationDeadline());
        }

        List<Internship> closingInternships = internshipRepository.findByDeadlineBetweenAndStatus(
                today, windowEnd, OpportunityStatus.OPEN);
        for (Internship i : closingInternships) {
            log.info("REMINDER: Internship at '{}' for role '{}' deadline is approaching on {}",
                    i.getCompany(), i.getRole(), i.getDeadline());
        }

        List<OffCampusJob> closingJobs = jobRepository.findByDeadlineBetweenAndStatus(
                today, windowEnd, OpportunityStatus.OPEN);
        for (OffCampusJob j : closingJobs) {
            log.info("REMINDER: Job opening at '{}' for role '{}' deadline is approaching on {}",
                    j.getCompany(), j.getRole(), j.getDeadline());
        }

        int totalReminders = closingHackathons.size() + closingInternships.size() + closingJobs.size();
        log.info("RemindersJob complete. Found {} opportunities closing within 3 days.", totalReminders);
    }
}
