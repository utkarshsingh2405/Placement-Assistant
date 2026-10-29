package com.placementassist.institute.modules.hackathon;

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
 * Seeds 15 prestigious national hackathons with detailed how-to-apply steps.
 */
@Component
@Order(7)
@ConditionalOnBean(name = "entityManagerFactory")
public class HackathonSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(HackathonSeedRunner.class);

    private final HackathonRepository hackathonRepository;

    public HackathonSeedRunner(HackathonRepository hackathonRepository) {
        this.hackathonRepository = hackathonRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (hackathonRepository.count() >= 15) {
            return;
        }

        log.info("Seeding 15 national and collegiate hackathons...");

        Object[][] data = {
                {"Smart India Hackathon 2025", "Ministry of Education & AICTE", OpportunityMode.HYBRID,
                 LocalDate.now().plusDays(40), LocalDate.now().plusDays(42), LocalDate.now().plusDays(20),
                 "₹1,00,000 per problem statement", 6, "New Delhi / Multi-City", "https://sih.gov.in",
                 "Nationwide initiative to provide students with a platform to solve pressing problems of society.",
                 "1. Form a college team of 6 members with at least one female teammate.\n2. Get college SPOC nomination letter.\n3. Submit problem statement idea PPT.\n4. Shortlisted teams attend Grand Finale.",
                 OpportunityStatus.OPEN},

                {"Flipkart GRiD 6.0", "Flipkart India", OpportunityMode.ONLINE,
                 LocalDate.now().plusDays(30), LocalDate.now().plusDays(35), LocalDate.now().plusDays(15),
                 "₹5,00,000 + SDE PPI Offers", 3, "Bengaluru / Online", "https://unstop.com/flipkart-grid",
                 "Flagship campus engineering challenge across Information Security, Robotics, and GenAI.",
                 "1. Register team on Unstop platform.\n2. Complete Level 1 technical quiz.\n3. Build prototype for Level 2 submission.\n4. Present live to Flipkart leadership.",
                 OpportunityStatus.OPEN},

                {"Tata Crucible Hackathon", "Tata Sons", OpportunityMode.HYBRID,
                 LocalDate.now().plusDays(45), LocalDate.now().plusDays(47), LocalDate.now().plusDays(25),
                 "₹2,50,000 Cash Prize", 4, "Mumbai", "https://tatacrucible.com",
                 "Solving industrial automation and sustainable supply chain problems.",
                 "1. Register on official Tata portal.\n2. Choose problem statement.\n3. Submit executive summary and prototype video.\n4. Finals in Mumbai.",
                 OpportunityStatus.OPEN},

                {"Walmart Sparkathon 2025", "Walmart Global Tech", OpportunityMode.ONLINE,
                 LocalDate.now().plusDays(35), LocalDate.now().plusDays(37), LocalDate.now().plusDays(10),
                 "₹3,00,000 + Summer Internships", 3, "Online / Chennai", "https://walmart.sparkathon.com",
                 "Retail tech innovation focusing on hyper-personalized customer journeys.",
                 "1. Register team of 2-3 members.\n2. Round 1 Online Coding Assessment.\n3. Round 2 Solution Architecture Pitch.\n4. Grand Finale Demo.",
                 OpportunityStatus.OPEN},

                {"Hack On with Amazon", "Amazon India", OpportunityMode.ONLINE,
                 LocalDate.now().plusDays(50), LocalDate.now().plusDays(52), LocalDate.now().plusDays(28),
                 "₹3,50,000 + Amazon Mentorship", 4, "Hyderabad / Online", "https://amazon.jobs/hackon",
                 "Build customer-obsessed AI/ML tools solving real customer delivery challenges.",
                 "1. Register on Amazon student careers portal.\n2. Submit project proposal and system architecture.\n3. Build MVP on AWS free tier.\n4. Virtual presentation.",
                 OpportunityStatus.OPEN},

                {"Google Solution Challenge 2025", "Google Developer Student Clubs", OpportunityMode.ONLINE,
                 LocalDate.now().plusDays(60), LocalDate.now().plusDays(62), LocalDate.now().plusDays(30),
                 "$10,000 + Google Mentorship", 4, "Global / Online", "https://developers.google.com/community/gdsc-solution-challenge",
                 "Solve for one or more of the United Nations 17 Sustainable Development Goals using Google tech.",
                 "1. Join college GDSC chapter.\n2. Select a UN SDG goal.\n3. Build solution using Flutter, TensorFlow, or Firebase.\n4. Submit demo video on YouTube.",
                 OpportunityStatus.OPEN},

                {"Microsoft Imagine Cup 2025", "Microsoft Corp", OpportunityMode.HYBRID,
                 LocalDate.now().plusDays(70), LocalDate.now().plusDays(73), LocalDate.now().plusDays(35),
                 "$100,000 Global Grand Prize", 4, "Online / Seattle Global Finals", "https://imaginecup.microsoft.com",
                 "Global student technology competition empowering students to create with AI.",
                 "1. Register with student Microsoft account.\n2. Pitch AI solution idea.\n3. Complete MVP with Azure OpenAI.\n4. Advance through regional to World Finals.",
                 OpportunityStatus.OPEN},

                {"Reliance TUP 9.0", "Reliance Industries", OpportunityMode.HYBRID,
                 LocalDate.now().plusDays(28), LocalDate.now().plusDays(30), LocalDate.now().plusDays(8),
                 "₹6,00,000 + PPO Opportunity", 3, "Navi Mumbai", "https://reliancetup.com",
                 "The Ultimate Pitch - innovate next-gen digital transformation solutions.",
                 "1. Form a cross-functional student team.\n2. Upload 3-minute video pitch.\n3. Semifinal pitch to business heads.\n4. Grand Finale at Reliance Corporate Park.",
                 OpportunityStatus.OPEN},

                {"Infosys Global Hackathon", "Infosys Ltd", OpportunityMode.ONLINE,
                 LocalDate.now().plusDays(55), LocalDate.now().plusDays(57), LocalDate.now().plusDays(22),
                 "₹2,00,000 + Specialist Programmer PPIs", 3, "Mysuru / Virtual", "https://infytq.onwingspan.com",
                 "Enterprise generative AI hackathon for future software engineers.",
                 "1. Complete registration on InfyTQ platform.\n2. Attempt online coding round.\n3. 36-hour virtual build phase.\n4. Jury evaluation.",
                 OpportunityStatus.OPEN},

                {"Cisco Ideathon 2025", "Cisco Systems", OpportunityMode.ONLINE,
                 LocalDate.now().plusDays(40), LocalDate.now().plusDays(42), LocalDate.now().plusDays(18),
                 "Pre-Placement Offers + Gear", 2, "Bengaluru / Online", "https://cisco.com/ideathon",
                 "Networking, Cybersecurity, and IoT infrastructure problem solving.",
                 "1. Complete NetAcad courses prerequisites.\n2. Ideation submission.\n3. Video interview & technical assessment.\n4. Final presentation to Cisco engineers.",
                 OpportunityStatus.OPEN},

                {"EY Techathon 5.0", "Ernst & Young", OpportunityMode.HYBRID,
                 LocalDate.now().plusDays(32), LocalDate.now().plusDays(34), LocalDate.now().plusDays(12),
                 "₹3,00,000 + EY GDS Internships", 4, "Gurugram / Online", "https://ey.com/techathon",
                 "Innovating for sustainable and resilient business ecosystems.",
                 "1. Register on Unstop EY page.\n2. Choose from Fintech or HealthTech tracks.\n3. Submit executive deck.\n4. Live presentation to EY leadership.",
                 OpportunityStatus.OPEN},

                {"Morgan Stanley Code to Give", "Morgan Stanley", OpportunityMode.HYBRID,
                 LocalDate.now().plusDays(45), LocalDate.now().plusDays(47), LocalDate.now().plusDays(20),
                 "₹2,50,000 + Fast-Track Interviews", 4, "Mumbai / Bengaluru", "https://morganstanley.com/codetogive",
                 "Hackathon dedicated to solving tech challenges for non-profit organizations.",
                 "1. Submit CV and Github profile.\n2. Virtual coding screening.\n3. 48-hour collaborative build with non-profit mentors.\n4. Final showcase.",
                 OpportunityStatus.OPEN},

                {"Barclays Rise Hackathon", "Barclays Bank", OpportunityMode.ONLINE,
                 LocalDate.now().plusDays(38), LocalDate.now().plusDays(40), LocalDate.now().plusDays(14),
                 "₹2,00,000 + Graduate Analyst PPI", 3, "Pune / Online", "https://rise.barclays",
                 "Next-generation digital payments and fraud prevention analytics.",
                 "1. Register team on platform.\n2. Hackathon round on secure transaction APIs.\n3. Demo presentation.\n4. Interview shortlisting.",
                 OpportunityStatus.OPEN},

                {"Optum Stratethon", "UnitedHealth Group", OpportunityMode.HYBRID,
                 LocalDate.now().plusDays(50), LocalDate.now().plusDays(52), LocalDate.now().plusDays(24),
                 "₹5,00,000 + PPI", 3, "Hyderabad", "https://optum.com/stratethon",
                 "Revolutionizing patient experience through AI and health informatics.",
                 "1. Register team in engineering track.\n2. Round 1 coding challenge.\n3. Prototype phase with Optum mentor.\n4. Grand Finale.",
                 OpportunityStatus.OPEN},

                {"Adobe India Hackathon", "Adobe", OpportunityMode.ONLINE,
                 LocalDate.now().plusDays(65), LocalDate.now().plusDays(67), LocalDate.now().plusDays(30),
                 "₹4,00,000 + Adobe Swag & PPI", 2, "Noida / Online", "https://adobe.com/careers/hackathon",
                 "Creative Cloud and Document Cloud automated workflows with GenAI.",
                 "1. Register team on Adobe careers page.\n2. Submit creative AI workflow prototype.\n3. Live demo and code walkthrough.\n4. Winners announcement.",
                 OpportunityStatus.OPEN}
        };

        for (Object[] row : data) {
            Hackathon h = Hackathon.builder()
                    .name((String) row[0])
                    .organizer((String) row[1])
                    .mode((OpportunityMode) row[2])
                    .startDate((LocalDate) row[3])
                    .endDate((LocalDate) row[4])
                    .registrationDeadline((LocalDate) row[5])
                    .prize((String) row[6])
                    .teamSize((Integer) row[7])
                    .location((String) row[8])
                    .link((String) row[9])
                    .description((String) row[10])
                    .howToApply((String) row[11])
                    .status((OpportunityStatus) row[12])
                    .build();
            hackathonRepository.save(h);
        }

        log.info("Hackathon seeding complete! Total hackathons: {}", hackathonRepository.count());
    }
}
