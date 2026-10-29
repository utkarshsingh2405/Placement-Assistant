package com.placementassist.institute.modules.companydrive;

import com.placementassist.institute.common.enums.DriveStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds at least 15 realistic campus recruiter companies and placement drives.
 */
@Component
@Order(2)
@ConditionalOnBean(name = "entityManagerFactory")
public class CompanyDriveSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CompanyDriveSeedRunner.class);

    private final CompanyRepository companyRepository;
    private final DriveRepository driveRepository;

    public CompanyDriveSeedRunner(CompanyRepository companyRepository, DriveRepository driveRepository) {
        this.companyRepository = companyRepository;
        this.driveRepository = driveRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (companyRepository.count() >= 15) {
            return;
        }

        log.info("Seeding 15 realistic campus recruiting companies and placement drives...");

        Object[][] companyData = {
                {"Google India", "https://careers.google.com", "Global technology leader in search, cloud, and AI.", "Technology", "Bengaluru", "https://logo.clearbit.com/google.com"},
                {"Microsoft IDC", "https://careers.microsoft.com", "Enterprise software, cloud computing and hardware.", "Technology", "Hyderabad", "https://logo.clearbit.com/microsoft.com"},
                {"Amazon Development Centre", "https://amazon.jobs", "E-commerce, cloud infrastructure, and AI innovations.", "Technology", "Hyderabad", "https://logo.clearbit.com/amazon.com"},
                {"Tata Consultancy Services (TCS)", "https://www.tcs.com/careers", "Leading global IT services, consulting and business solutions.", "IT Services", "Mumbai", "https://logo.clearbit.com/tcs.com"},
                {"Infosys Ltd", "https://www.infosys.com/careers", "Next-generation digital services and consulting.", "IT Services", "Bengaluru", "https://logo.clearbit.com/infosys.com"},
                {"Wipro Technologies", "https://careers.wipro.com", "IT, consulting, and business process services company.", "IT Services", "Bengaluru", "https://logo.clearbit.com/wipro.com"},
                {"Cognizant", "https://careers.cognizant.com", "Modernizing technology, reimagining processes and transforming experiences.", "IT Services", "Chennai", "https://logo.clearbit.com/cognizant.com"},
                {"Accenture India", "https://www.accenture.com/in-en/careers", "Global professional services company with leading digital capabilities.", "Consulting & IT", "Bengaluru", "https://logo.clearbit.com/accenture.com"},
                {"Deloitte USI", "https://www2.deloitte.com/ui/en/careers", "Audit, consulting, advisory, and tax services.", "Consulting", "Hyderabad", "https://logo.clearbit.com/deloitte.com"},
                {"Qualcomm India", "https://www.qualcomm.com/company/careers", "World leader in wireless tech, semiconductors, and telecommunications.", "Semiconductors", "Bengaluru", "https://logo.clearbit.com/qualcomm.com"},
                {"Cisco Systems", "https://www.cisco.com/c/en/us/about/careers.html", "Worldwide technology leader that connects everything securely.", "Networking", "Bengaluru", "https://logo.clearbit.com/cisco.com"},
                {"Oracle India", "https://www.oracle.com/in/corporate/careers", "Database management systems and cloud engineered systems.", "Enterprise Tech", "Bengaluru", "https://logo.clearbit.com/oracle.com"},
                {"Zoho Corporation", "https://www.zoho.com/careers", "Privately held Indian software development company.", "Product Software", "Chennai", "https://logo.clearbit.com/zoho.com"},
                {"Flipkart", "https://www.flipkartcareers.com", "India's homegrown e-commerce marketplace.", "E-commerce", "Bengaluru", "https://logo.clearbit.com/flipkart.com"},
                {"Capgemini India", "https://www.capgemini.com/in-en/careers", "Global leader in partnering with companies to transform through technology.", "IT Services", "Pune", "https://logo.clearbit.com/capgemini.com"}
        };

        List<Company> savedCompanies = new ArrayList<>();
        for (Object[] row : companyData) {
            String name = (String) row[0];
            Company company = companyRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> companyRepository.save(Company.builder()
                            .name(name)
                            .website((String) row[1])
                            .about((String) row[2])
                            .industry((String) row[3])
                            .headquarters((String) row[4])
                            .logoUrl((String) row[5])
                            .build()));
            savedCompanies.add(company);
        }

        // Seed 15 Drives
        Object[][] driveData = {
                {0, "Software Development Engineer - 2025", 8.0, "CSE,IT", 0, "SDE-1", 18.0, 32.0, "Bengaluru", 14, 7, DriveStatus.UPCOMING},
                {1, "Software Engineer (Full-Stack / Cloud)", 7.5, "CSE,IT,ECE", 0, "Software Engineer, Cloud Engineer", 16.0, 28.0, "Hyderabad", 18, 10, DriveStatus.UPCOMING},
                {2, "Quality Assurance & SDE Intern to Full-Time", 7.0, "CSE,IT", 0, "SDE, Systems Engineer", 14.0, 24.0, "Hyderabad", 21, 12, DriveStatus.UPCOMING},
                {3, "TCS Digital & Ninja National Qualifier Drive", 6.0, "CSE,IT,ECE,EEE,MECH,CIVIL", 1, "Assistant System Engineer, Systems Engineer", 3.6, 7.5, "Pan India", 30, 20, DriveStatus.UPCOMING},
                {4, "Specialist Programmer & Digital Specialist", 6.0, "CSE,IT,ECE", 0, "Specialist Programmer, DSE", 6.5, 9.5, "Bengaluru / Pune", 35, 25, DriveStatus.UPCOMING},
                {5, "Turbo & Elite Developer Hiring 2025", 6.0, "CSE,IT,ECE,EEE", 1, "Project Engineer", 4.0, 6.5, "Bengaluru / Chennai", 40, 30, DriveStatus.UPCOMING},
                {6, "GenC Next Developer Campus Recruitment", 6.5, "CSE,IT", 0, "GenC Next Engineer", 4.5, 7.0, "Chennai", 45, 32, DriveStatus.UPCOMING},
                {7, "Advanced Application Engineering Analyst", 6.5, "CSE,IT,ECE", 0, "Application Developer, Cloud Associate", 4.5, 6.5, "Bengaluru / Gurugram", 25, 15, DriveStatus.UPCOMING},
                {8, "Technology Analyst - USI Consulting", 7.0, "CSE,IT,ECE", 0, "Technology Consultant", 7.6, 10.5, "Hyderabad / Bengaluru", 20, 11, DriveStatus.UPCOMING},
                {9, "Hardware & Embedded Firmware Engineer", 7.5, "ECE,EEE", 0, "Firmware Engineer, ASIC Design Engineer", 12.0, 20.0, "Bengaluru", 28, 16, DriveStatus.UPCOMING},
                {10, "Network Software Engineer Drive", 7.5, "CSE,IT,ECE", 0, "Software Engineer - Networking", 15.0, 22.0, "Bengaluru", 32, 19, DriveStatus.UPCOMING},
                {11, "Associate Applications Developer", 7.0, "CSE,IT", 0, "Database Engineer, Cloud Developer", 11.0, 18.0, "Bengaluru", 38, 22, DriveStatus.UPCOMING},
                {12, "Product Developer & QA Trainee Hiring", 6.5, "CSE,IT,ECE,EEE,MECH", 0, "Software Developer", 6.0, 12.0, "Chennai / Tenkasi", 12, 5, DriveStatus.UPCOMING},
                {13, "Associate SDE - Supply Chain & Marketplace", 8.0, "CSE,IT", 0, "Associate SDE", 18.0, 26.0, "Bengaluru", 16, 8, DriveStatus.UPCOMING},
                {14, "Analyst & Senior Analyst Campus Drive", 6.0, "CSE,IT,ECE,EEE", 1, "Senior Analyst", 4.25, 5.75, "Pune / Mumbai", 50, 35, DriveStatus.UPCOMING}
        };

        LocalDate today = LocalDate.now();
        for (int i = 0; i < driveData.length; i++) {
            Object[] d = driveData[i];
            int compIdx = (int) d[0];
            Company company = savedCompanies.get(compIdx);

            String title = (String) d[1];
            double cgpa = (double) d[2];
            String branches = (String) d[3];
            int backlogs = (int) d[4];
            String roles = (String) d[5];
            double pkgMin = (double) d[6];
            double pkgMax = (double) d[7];
            String location = (String) d[8];
            int driveDaysAhead = (int) d[9];
            int applyDaysAhead = (int) d[10];
            DriveStatus status = (DriveStatus) d[11];

            Drive drive = Drive.builder()
                    .company(company)
                    .title(title)
                    .driveDate(today.plusDays(driveDaysAhead))
                    .lastDateToApply(today.plusDays(applyDaysAhead))
                    .eligibilityCgpa(cgpa)
                    .eligibleBranches(branches)
                    .maxBacklogs(backlogs)
                    .roles(roles)
                    .packageMin(pkgMin)
                    .packageMax(pkgMax)
                    .location(location)
                    .status(status)
                    .description("Campus hiring drive for " + company.getName() + " for 2025 graduating batch.")
                    .build();

            driveRepository.save(drive);
        }

        log.info("Seeded 15 sample placement drives successfully.");
    }
}
