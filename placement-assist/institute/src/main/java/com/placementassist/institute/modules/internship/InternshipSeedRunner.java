package com.placementassist.institute.modules.internship;

import com.placementassist.institute.common.enums.OpportunityMode;
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
 * Seeds 15 prestigious corporate and tech internship listings across India.
 */
@Component
@Order(8)
@ConditionalOnBean(name = "entityManagerFactory")
public class InternshipSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InternshipSeedRunner.class);

    private final InternshipRepository internshipRepository;

    public InternshipSeedRunner(InternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (internshipRepository.count() >= 15) {
            return;
        }

        log.info("Seeding 15 corporate and research internship opportunities...");

        Object[][] data = {
                {"Google India", "Software Engineering Summer Intern", 110000.0, 3, "Bengaluru / Hyderabad", OpportunityMode.HYBRID,
                 "B.Tech CSE/IT 3rd Year with CGPA >= 8.0 and no active backlogs", LocalDate.now().plusDays(25),
                 "https://careers.google.com/jobs/results/intern",
                 "1. Submit updated resume.\n2. Complete Google Online Challenge (DSA).\n3. Two 45-min technical interviews on Data Structures & Algorithms.\n4. Project host matching.",
                 "Java, C++, Python, Data Structures, Algorithms", OpportunityStatus.OPEN},

                {"Microsoft", "Software Engineering Intern", 125000.0, 2, "Hyderabad / Bengaluru", OpportunityMode.HYBRID,
                 "Pre-final year engineering students (2026 batch)", LocalDate.now().plusDays(20),
                 "https://careers.microsoft.com",
                 "1. Apply via Microsoft university careers link.\n2. Online assessment with 3 coding questions.\n3. Virtual interview rounds covering System Design & OS.",
                 "C#, C++, Java, Cloud Computing, Azure", OpportunityStatus.OPEN},

                {"Amazon", "SDE Intern (6 Months)", 80000.0, 6, "Hyderabad", OpportunityMode.OFFLINE,
                 "Final year students eligible for 6-month semester internship", LocalDate.now().plusDays(15),
                 "https://amazon.jobs",
                 "1. Apply with College NOC for full-time semester internship.\n2. Amazon Online Assessment (OAA 1 & 2).\n3. Technical Bar-Raiser interview.",
                 "Java, Spring Boot, Microservices, AWS, SQL", OpportunityStatus.OPEN},

                {"Uber", "Software Engineer Intern", 160000.0, 3, "Bengaluru", OpportunityMode.HYBRID,
                 "B.Tech/Dual Degree 3rd year students", LocalDate.now().plusDays(30),
                 "https://uber.com/careers",
                 "1. Online coding round on HackerEarth.\n2. Problem Solving & System Architecture round.\n3. Hiring manager cultural fitment.",
                 "Go, Java, Distributed Systems, Kafka", OpportunityStatus.OPEN},

                {"Goldman Sachs", "Summer Analyst - Technology", 100000.0, 2, "Bengaluru / Hyderabad", OpportunityMode.OFFLINE,
                 "Pre-final year students from all engineering branches", LocalDate.now().plusDays(18),
                 "https://goldmansachs.com/careers/students",
                 "1. Register on Goldman Sachs portal.\n2. Aptitude & Advanced Math + Coding assessment.\n3. Technical & Behavioral interviews.",
                 "Java, Python, C++, Data Analytics, Finance Tech", OpportunityStatus.OPEN},

                {"Cisco Systems", "Software Consulting Engineer Intern", 60000.0, 6, "Bengaluru", OpportunityMode.HYBRID,
                 "CSE, IT, ECE with CGPA >= 7.5", LocalDate.now().plusDays(35),
                 "https://jobs.cisco.com",
                 "1. Cisco NetAcad assessment.\n2. Python coding & Networking fundamentals test.\n3. Technical interview with Cisco engineering managers.",
                 "Python, Computer Networks, Linux, APIs", OpportunityStatus.OPEN},

                {"DE Shaw & Co", "Quality & Software Operations Intern", 150000.0, 2, "Hyderabad", OpportunityMode.OFFLINE,
                 "Top tier academic records (CGPA >= 8.5)", LocalDate.now().plusDays(12),
                 "https://deshawindia.com",
                 "1. Resume screening.\n2. Rigorous algorithmic test.\n3. Multiple rounds of DSA & Quantitative problem solving.",
                 "Python, C++, SQL, Algorithms, Linux", OpportunityStatus.OPEN},

                {"Salesforce", "Software Engineering Intern", 90000.0, 2, "Hyderabad", OpportunityMode.HYBRID,
                 "3rd year students in computer engineering", LocalDate.now().plusDays(22),
                 "https://salesforce.com/careers",
                 "1. HackerRank assessment.\n2. Two technical rounds focusing on OOP and Concurrency.\n3. Leadership principles round.",
                 "Java, JavaScript, React, Salesforce Platform", OpportunityStatus.OPEN},

                {"Adobe", "Research Intern - Generative AI", 100000.0, 3, "Noida / Bengaluru", OpportunityMode.HYBRID,
                 "B.Tech / M.Tech / MS students with ML coursework", LocalDate.now().plusDays(40),
                 "https://adobe.com/careers",
                 "1. Submit CV highlighting GitHub/Kaggle/Publications.\n2. Coding test in Python/C++.\n3. Technical presentation on prior ML projects.",
                 "Python, PyTorch, Computer Vision, Transformers", OpportunityStatus.OPEN},

                {"Oracle", "Cloud Infrastructure (OCI) Intern", 65000.0, 6, "Bengaluru", OpportunityMode.OFFLINE,
                 "Final year students eligible for 6-month internship", LocalDate.now().plusDays(28),
                 "https://oracle.com/careers",
                 "1. Online assessment on Cloud & OS concepts.\n2. Technical interview on Operating Systems, Linux and Java.",
                 "Java, Linux, Distributed Systems, Cloud Architecture", OpportunityStatus.OPEN},

                {"Morgan Stanley", "Technology Summer Analyst", 85000.0, 2, "Mumbai", OpportunityMode.OFFLINE,
                 "Pre-final year B.Tech students", LocalDate.now().plusDays(32),
                 "https://morganstanley.com/careers",
                 "1. Coding round with LeetCode medium questions.\n2. Two technical interviews on OOP, DBMS, and Data Structures.\n3. HR round.",
                 "Java, C++, SQL, Data Structures, OOP", OpportunityStatus.OPEN},

                {"SAP Labs", "Developer Associate Intern", 45000.0, 6, "Bengaluru", OpportunityMode.HYBRID,
                 "CSE/IT/ECE 8th semester eligible students", LocalDate.now().plusDays(14),
                 "https://sap.com/careers",
                 "1. SAP assessment.\n2. Technical interview on Full-Stack Development.\n3. Managerial discussion.",
                 "Java, Spring, Node.js, Cloud Foundry", OpportunityStatus.OPEN},

                {"Barclays", "Technology Graduate Intern", 75000.0, 2, "Pune", OpportunityMode.HYBRID,
                 "B.Tech pre-final year with no backlogs", LocalDate.now().plusDays(26),
                 "https://barclays.com/careers",
                 "1. SHL online cognitive & coding test.\n2. Virtual assessment center with coding interview.\n3. Offer rollout.",
                 "Java, Python, Microservices, Angular", OpportunityStatus.OPEN},

                {"Qualcomm", "Systems Software Engineering Intern", 55000.0, 6, "Hyderabad / Chennai", OpportunityMode.OFFLINE,
                 "ECE / CSE with strong C/C++ knowledge", LocalDate.now().plusDays(38),
                 "https://qualcomm.com/careers",
                 "1. C Programming and Embedded Systems MCQ + Coding.\n2. Technical interview on OS and Memory Architecture.",
                 "C, C++, Embedded Systems, Linux Kernel, RTOS", OpportunityStatus.OPEN},

                {"Swiggy", "Software Development Intern - Consumer Tech", 50000.0, 6, "Bengaluru", OpportunityMode.HYBRID,
                 "Final year students passionate about consumer hyper-local platforms", LocalDate.now().plusDays(16),
                 "https://careers.swiggy.com",
                 "1. Submit profile and project demos.\n2. Live pair programming round on DSA.\n3. Engineering manager fitment.",
                 "Java, Go, Redis, Microservices, MySQL", OpportunityStatus.OPEN}
        };

        for (Object[] row : data) {
            Internship i = Internship.builder()
                    .company((String) row[0])
                    .role((String) row[1])
                    .stipend((Double) row[2])
                    .durationMonths((Integer) row[3])
                    .location((String) row[4])
                    .mode((OpportunityMode) row[5])
                    .eligibility((String) row[6])
                    .deadline((LocalDate) row[7])
                    .applyUrl((String) row[8])
                    .howToApply((String) row[9])
                    .skillsRequired((String) row[10])
                    .status((OpportunityStatus) row[11])
                    .build();
            internshipRepository.save(i);
        }

        log.info("Internship seeding complete! Total internships: {}", internshipRepository.count());
    }
}
