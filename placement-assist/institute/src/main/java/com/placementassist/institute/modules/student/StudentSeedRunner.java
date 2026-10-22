package com.placementassist.institute.modules.student;

import com.placementassist.institute.modules.auth.User;
import com.placementassist.institute.modules.auth.UserRepository;
import com.placementassist.shared.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds default administrative accounts and realistic sample Indian college student records.
 */
@Component
@Order(1)
@ConditionalOnBean(name = "entityManagerFactory")
public class StudentSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StudentSeedRunner.class);

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentSeedRunner(UserRepository userRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Seed Admin
        if (!userRepository.existsByEmail("admin@college.edu")) {
            User admin = User.builder()
                    .email("admin@college.edu")
                    .passwordHash(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Seeded default ADMIN: admin@college.edu / Admin@123");
        }

        // 2. Seed TPO
        if (!userRepository.existsByEmail("tpo@college.edu")) {
            User tpo = User.builder()
                    .email("tpo@college.edu")
                    .passwordHash(passwordEncoder.encode("Tpo@123"))
                    .role(Role.TPO)
                    .build();
            userRepository.save(tpo);
            log.info("Seeded default TPO: tpo@college.edu / Tpo@123");
        }

        // 3. Seed 15 Realistic Indian College Students if empty
        if (studentRepository.count() >= 15) {
            return;
        }

        log.info("Seeding realistic Indian-college students...");

        Object[][] sampleData = {
                {"Aarav Sharma", "21CS01", "aarav.sharma@college.edu", "CSE", 2025, 8.85, 0, List.of("Java", "Spring Boot", "React", "PostgreSQL"), "+91 9876543210"},
                {"Diya Patel", "21CS02", "diya.patel@college.edu", "CSE", 2025, 9.20, 0, List.of("Python", "Machine Learning", "FastAPI", "Docker"), "+91 9876543211"},
                {"Rohan Verma", "21CS03", "rohan.verma@college.edu", "CSE", 2025, 7.45, 0, List.of("Node.js", "Express", "MongoDB", "Angular"), "+91 9876543212"},
                {"Ananya Iyer", "21IT01", "ananya.iyer@college.edu", "IT", 2025, 8.60, 0, List.of("Java", "Kotlin", "Android", "Firebase"), "+91 9876543213"},
                {"Ishaan Nair", "21IT02", "ishaan.nair@college.edu", "IT", 2025, 6.90, 0, List.of("C++", "DSA", "SQL", "Git"), "+91 9876543214"},
                {"Pooja Reddy", "21ECE01", "pooja.reddy@college.edu", "ECE", 2025, 8.10, 0, List.of("Embedded C", "IoT", "Arduino", "Python"), "+91 9876543215"},
                {"Karthik Sundaram", "21ECE02", "karthik.s@college.edu", "ECE", 2025, 5.80, 2, List.of("VLSI", "Verilog", "MATLAB"), "+91 9876543216"},
                {"Sneha Kulkarni", "21CS04", "sneha.k@college.edu", "CSE", 2025, 8.95, 0, List.of("Go", "Kubernetes", "AWS", "Microservices"), "+91 9876543217"},
                {"Aditya Joshi", "21MECH01", "aditya.joshi@college.edu", "MECH", 2025, 7.80, 0, List.of("AutoCAD", "SolidWorks", "Python", "Ansys"), "+91 9876543218"},
                {"Meera Menon", "21IT03", "meera.menon@college.edu", "IT", 2025, 7.15, 0, List.of("HTML/CSS", "JavaScript", "Vue.js", "MySQL"), "+91 9876543219"},
                {"Vikram Singh", "21EEE01", "vikram.singh@college.edu", "EEE", 2025, 6.40, 1, List.of("Power Systems", "PLC", "MATLAB"), "+91 9876543220"},
                {"Tanvi Deshmukh", "21CS05", "tanvi.d@college.edu", "CSE", 2025, 8.75, 0, List.of("React Native", "TypeScript", "Node.js", "Redux"), "+91 9876543221"},
                {"Arjun Gupta", "21ECE03", "arjun.gupta@college.edu", "ECE", 2025, 7.60, 0, List.of("Signal Processing", "C++", "Linux", "ROS"), "+91 9876543222"},
                {"Neha Roy", "21CIVIL01", "neha.roy@college.edu", "CIVIL", 2025, 8.30, 0, List.of("Revit", "STAAD Pro", "GIS", "Python"), "+91 9876543223"},
                {"Rishi Mukherjee", "21CS06", "rishi.m@college.edu", "CSE", 2025, 5.50, 0, List.of("PHP", "MySQL", "JavaScript"), "+91 9876543224"}
        };

        for (Object[] data : sampleData) {
            String name = (String) data[0];
            String rollNo = (String) data[1];
            String email = (String) data[2];
            String branch = (String) data[3];
            int year = (int) data[4];
            double cgpa = (double) data[5];
            int backlogs = (int) data[6];
            @SuppressWarnings("unchecked")
            List<String> skills = (List<String>) data[7];
            String phone = (String) data[8];

            if (!userRepository.existsByEmail(email)) {
                User studentUser = User.builder()
                        .email(email)
                        .passwordHash(passwordEncoder.encode("Student@123"))
                        .role(Role.STUDENT)
                        .build();
                studentUser = userRepository.save(studentUser);

                boolean eligible = cgpa >= 6.0 && backlogs == 0;

                Student student = Student.builder()
                        .user(studentUser)
                        .name(name)
                        .rollNo(rollNo)
                        .email(email)
                        .branch(branch)
                        .year(year)
                        .cgpa(cgpa)
                        .backlogs(backlogs)
                        .skills(skills)
                        .phone(phone)
                        .placementEligible(eligible)
                        .linkedin("https://linkedin.com/in/" + rollNo.toLowerCase())
                        .github("https://github.com/" + rollNo.toLowerCase())
                        .build();
                studentRepository.save(student);
            }
        }

        log.info("Seeded 15 sample students successfully.");
    }
}
