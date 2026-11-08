package com.placementassist.institute.modules.offcampusjob;

import com.placementassist.institute.common.enums.OpportunityStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Seeds 15 high-growth tech startup and product off-campus job listings.
 */
@Component
@Order(9)
@ConditionalOnBean(name = "entityManagerFactory")
public class OffCampusJobSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(OffCampusJobSeedRunner.class);

    private final OffCampusJobRepository jobRepository;

    public OffCampusJobSeedRunner(OffCampusJobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (jobRepository.count() >= 15) {
            return;
        }

        log.info("Seeding 15 verified off-campus fresher job openings...");

        Object[][] data = {
                {"Atlassian", "Associate Software Engineer", 16.0, "Bengaluru", "2024, 2025", "Fresher / 0-1 Years",
                 "Java, Kotlin, React, AWS, Microservices", LocalDate.now().plusDays(25),
                 "https://atlassian.com/company/careers",
                 "1. Submit application on Atlassian Careers.\n2. HackerRank coding assessment (90 mins).\n3. System Design & Coding virtual onsite.\n4. Values interview.",
                 OpportunityStatus.OPEN},

                {"Razorpay", "Software Development Engineer 1", 15.0, "Bengaluru", "2024, 2025", "Fresher / 0-1 Years",
                 "Go, Python, MySQL, Kafka, Redis", LocalDate.now().plusDays(20),
                 "https://razorpay.com/jobs",
                 "1. Apply via career page link.\n2. Online technical challenge.\n3. Technical Round 1 on DSA and concurrency.\n4. Technical Round 2 on Database design.\n5. Cultural fitment.",
                 OpportunityStatus.OPEN},

                {"CRED", "Backend Engineer - Payments", 18.0, "Bengaluru", "2024, 2025", "Fresher / 0-1 Years",
                 "Java, Spring Boot, PostgreSQL, Kafka, Distributed Systems", LocalDate.now().plusDays(18),
                 "https://cred.club/careers",
                 "1. Online assessment with advanced DSA questions.\n2. Machine Coding round (build clean low-level design in 90 mins).\n3. Problem solving & behavioral round.",
                 OpportunityStatus.OPEN},

                {"PhonePe", "Software Engineer - Core Platform", 14.5, "Bengaluru", "2024, 2025", "Fresher",
                 "Java, Spring, Aerospike, Distributed Systems", LocalDate.now().plusDays(15),
                 "https://phonepe.com/careers",
                 "1. Online coding screening.\n2. 2 rounds of Technical Interviews on DSA.\n3. LLD and architecture design discussion.\n4. HR discussion.",
                 OpportunityStatus.OPEN},

                {"Zomato", "Software Development Engineer 1", 14.0, "Gurugram", "2024, 2025", "Fresher",
                 "Node.js, Go, MySQL, Redis, AWS", LocalDate.now().plusDays(28),
                 "https://zomato.com/careers",
                 "1. Resume screening and GitHub audit.\n2. Hackerearth coding test.\n3. Live technical pair programming.\n4. Leadership alignment.",
                 OpportunityStatus.OPEN},

                {"Juspay", "Software Development Engineer (Haskell / FP)", 15.0, "Bengaluru", "2024, 2025", "Fresher",
                 "Functional Programming, PureScript, Haskell, Rust, Algorithms", LocalDate.now().plusDays(12),
                 "https://juspay.in/careers",
                 "1. Juspay Hiring Challenge (Graph and Tree problems).\n2. 2-day hackathon or trial project.\n3. Technical interview on logic and category theory concepts.",
                 OpportunityStatus.OPEN},

                {"Groww", "Backend Engineer - Wealth Tech", 16.0, "Bengaluru", "2024, 2025", "Fresher / 0-1 Years",
                 "Java, Spring Boot, Microservices, Redis, MongoDB", LocalDate.now().plusDays(30),
                 "https://groww.in/careers",
                 "1. Apply online.\n2. Online coding test.\n3. Low Level Design (LLD) interview.\n4. Engineering Director interview.",
                 OpportunityStatus.OPEN},

                {"Zeta Suite", "Software Development Engineer", 17.5, "Bengaluru", "2024, 2025", "Fresher",
                 "Java, Distributed Storage, Banking APIs, Multithreading", LocalDate.now().plusDays(22),
                 "https://zeta.tech/careers",
                 "1. HackerRank coding assessment.\n2. Live data structures round.\n3. Object Oriented Design & concurrency round.\n4. Culture round.",
                 OpportunityStatus.OPEN},

                {"Urban Company", "Software Engineer 1", 15.0, "Gurugram", "2024, 2025", "Fresher",
                 "Node.js, Python, PostgreSQL, Microservices, Docker", LocalDate.now().plusDays(24),
                 "https://urbancompany.com/careers",
                 "1. Submit application with portfolio.\n2. Coding test.\n3. Technical round on system design and APIs.\n4. Manager interview.",
                 OpportunityStatus.OPEN},

                {"Meesho", "SDE 1 - Supply Chain Tech", 18.0, "Bengaluru", "2024, 2025", "Fresher / 0-1 Years",
                 "Java, Spring, Kafka, MySQL, Kubernetes", LocalDate.now().plusDays(16),
                 "https://meesho.io/careers",
                 "1. Online assessment.\n2. Problem Solving & Data Structures round.\n3. Machine Coding round.\n4. Bar Raiser interview.",
                 OpportunityStatus.OPEN},

                {"Zepto", "Backend Engineer - High Speed Logistics", 16.5, "Bengaluru", "2024, 2025", "Fresher",
                 "Go, Python, Redis, Postgres, Event Driven Architecture", LocalDate.now().plusDays(14),
                 "https://zeptonow.com/careers",
                 "1. Submit application.\n2. Algorithmic screening test.\n3. Technical interview with Staff Engineer.\n4. CTO / Head of Engineering round.",
                 OpportunityStatus.OPEN},

                {"Paytm", "Graduate Engineer Trainee", 9.5, "Noida", "2024, 2025", "Fresher",
                 "Java, Spring, Oracle DB, REST APIs", LocalDate.now().plusDays(35),
                 "https://paytm.com/careers",
                 "1. Online aptitude and coding test on CoCubes/Mettl.\n2. Technical interview on Java and Database queries.\n3. HR discussion.",
                 OpportunityStatus.OPEN},

                {"Delhivery", "Software Development Engineer", 12.0, "Gurugram", "2024, 2025", "Fresher / 0-1 Years",
                 "Python, Django, PostgreSQL, Celery, Docker", LocalDate.now().plusDays(26),
                 "https://delhivery.com/careers",
                 "1. Online coding round.\n2. Technical interview on Python, DSA and SQL.\n3. Engineering manager discussion.",
                 OpportunityStatus.OPEN},

                {"Postman", "Associate Software Engineer", 16.0, "Bengaluru", "2024, 2025", "Fresher",
                 "JavaScript, TypeScript, Node.js, Electron, Web APIs", LocalDate.now().plusDays(32),
                 "https://postman.com/careers",
                 "1. Apply via Postman careers portal.\n2. Coding assessment on JavaScript/Node.\n3. Pair programming technical interview.\n4. Culture fitment.",
                 OpportunityStatus.OPEN},

                {"BrowserStack", "Software Engineer - Cloud Infrastructure", 17.0, "Mumbai", "2024, 2025", "Fresher",
                 "Ruby, Go, Linux Internals, AWS, Docker", LocalDate.now().plusDays(21),
                 "https://browserstack.com/careers",
                 "1. HackerRank algorithmic assessment.\n2. Technical round 1 on OS and Network fundamentals.\n3. Technical round 2 on System Architecture.\n4. Leadership interview.",
                 OpportunityStatus.OPEN}
        };

        for (Object[] row : data) {
            OffCampusJob j = OffCampusJob.builder()
                    .company((String) row[0])
                    .role((String) row[1])
                    .packageLpa((Double) row[2])
                    .location((String) row[3])
                    .batchEligible((String) row[4])
                    .experience((String) row[5])
                    .skillsRequired((String) row[6])
                    .deadline((LocalDate) row[7])
                    .applyUrl((String) row[8])
                    .howToApply((String) row[9])
                    .status((OpportunityStatus) row[10])
                    .build();
            jobRepository.save(j);
        }

        log.info("Off-campus jobs seeding complete! Total jobs: {}", jobRepository.count());
    }
}
