package com.placementassist.institute.modules.resume;

import com.placementassist.institute.common.enums.ResumeStatus;
import com.placementassist.institute.modules.resume.dto.AtsBreakdownDto;
import com.placementassist.institute.modules.student.Student;
import com.placementassist.institute.modules.student.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds sample student resumes with pre-computed ATS scores and realistic breakdown JSONs.
 */
@Component
@Order(6)
@ConditionalOnBean(name = "entityManagerFactory")
public class ResumeSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ResumeSeedRunner.class);

    private final ResumeRepository resumeRepository;
    private final StudentRepository studentRepository;
    private final AtsScorer atsScorer;

    public ResumeSeedRunner(ResumeRepository resumeRepository, StudentRepository studentRepository, AtsScorer atsScorer) {
        this.resumeRepository = resumeRepository;
        this.studentRepository = studentRepository;
        this.atsScorer = atsScorer;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (resumeRepository.count() >= 10) {
            return;
        }

        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            log.warn("No students found. Skipping resume seed.");
            return;
        }

        log.info("Seeding realistic sample student resumes and ATS scores...");

        for (int i = 0; i < Math.min(12, students.size()); i++) {
            Student student = students.get(i);

            int baseScore = 65 + (i * 3) % 30; // 65 to 94
            int kwScore = Math.min(40, (int) (baseScore * 0.4));
            int secScore = (baseScore > 75) ? 30 : 24;
            int fmtScore = (baseScore > 80) ? 18 : 12;
            int actScore = baseScore - (kwScore + secScore + fmtScore);

            AtsBreakdownDto breakdown = new AtsBreakdownDto(
                    baseScore,
                    kwScore,
                    secScore,
                    fmtScore,
                    actScore,
                    List.of("java", "spring", "sql", "git", "rest", "docker"),
                    (baseScore < 80) ? List.of("aws", "microservices", "kubernetes") : List.of(),
                    List.of("Education", "Skills", "Projects", "Experience", "Contact"),
                    (secScore < 30) ? List.of("Experience") : List.of(),
                    (baseScore >= 80)
                            ? List.of("Excellent resume formatting! High ATS compliance.")
                            : List.of("Add more quantified metrics to your project descriptions.", "Incorporate AWS cloud keywords."),
                    520
            );

            ResumeStatus status;
            String feedback = null;
            String reviewer = null;

            if (baseScore >= 85) {
                status = ResumeStatus.APPROVED;
                feedback = "Strong resume structure and project highlights. Approved for upcoming Tier-1 product drives.";
                reviewer = "tpo@placementassist.edu";
            } else if (baseScore >= 75) {
                status = ResumeStatus.REVIEWED;
                feedback = "Good baseline. Emphasize your contributions in your Spring Boot project with metrics.";
                reviewer = "tpo@placementassist.edu";
            } else {
                status = ResumeStatus.PENDING;
            }

            Resume resume = Resume.builder()
                    .student(student)
                    .fileUrl("student_" + student.getRollNo() + "/resume_v1.pdf")
                    .version(1)
                    .status(status)
                    .atsScore(baseScore)
                    .atsBreakdown(atsScorer.toJson(breakdown))
                    .feedback(feedback)
                    .reviewedBy(reviewer)
                    .build();

            resumeRepository.save(resume);
        }

        log.info("Resume seeding complete! Total resumes: {}", resumeRepository.count());
    }
}
