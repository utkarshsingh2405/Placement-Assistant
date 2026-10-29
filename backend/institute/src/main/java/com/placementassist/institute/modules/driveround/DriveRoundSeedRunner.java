package com.placementassist.institute.modules.driveround;

import com.placementassist.institute.common.enums.RoundResultStatus;
import com.placementassist.institute.common.enums.RoundType;
import com.placementassist.institute.modules.companydrive.Drive;
import com.placementassist.institute.modules.companydrive.DriveRepository;
import com.placementassist.institute.modules.student.Student;
import com.placementassist.institute.modules.student.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Seeds evaluation rounds and realistic candidate evaluation results across placement drives.
 */
@Component
@Order(3)
@ConditionalOnBean(name = "entityManagerFactory")
public class DriveRoundSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DriveRoundSeedRunner.class);

    private final DriveRepository driveRepository;
    private final DriveRoundRepository driveRoundRepository;
    private final StudentRepository studentRepository;
    private final RoundResultRepository roundResultRepository;

    public DriveRoundSeedRunner(
            DriveRepository driveRepository,
            DriveRoundRepository driveRoundRepository,
            StudentRepository studentRepository,
            RoundResultRepository roundResultRepository) {
        this.driveRepository = driveRepository;
        this.driveRoundRepository = driveRoundRepository;
        this.studentRepository = studentRepository;
        this.roundResultRepository = roundResultRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (driveRoundRepository.count() >= 15) {
            return;
        }

        List<Drive> drives = driveRepository.findAll();
        List<Student> students = studentRepository.findAll();

        if (drives.isEmpty() || students.isEmpty()) {
            return;
        }

        log.info("Seeding evaluation rounds and round results for drives...");

        LocalDate today = LocalDate.now();

        // Seed multi-stage rounds for first 5 drives (3 rounds each = 15 rounds)
        for (int i = 0; i < Math.min(5, drives.size()); i++) {
            Drive drive = drives.get(i);

            // Round 1: Aptitude
            DriveRound r1 = driveRoundRepository.save(DriveRound.builder()
                    .drive(drive)
                    .roundType(RoundType.APTITUDE)
                    .orderNo(1)
                    .roundDate(today.plusDays(i * 2 + 1))
                    .venue("Online Testing Center / Hall A")
                    .durationMins(60)
                    .cutoff(70.0)
                    .instructions("Quantitative, Logical Reasoning, and Verbal Ability (60 questions).")
                    .build());

            // Round 2: Technical
            DriveRound r2 = driveRoundRepository.save(DriveRound.builder()
                    .drive(drive)
                    .roundType(RoundType.TECHNICAL)
                    .orderNo(2)
                    .roundDate(today.plusDays(i * 2 + 2))
                    .venue("CSE Lab 3 / Virtual Meet")
                    .durationMins(45)
                    .cutoff(75.0)
                    .instructions("Data Structures, Algorithms, System Design, and Live Coding.")
                    .build());

            // Round 3: Managerial / HR
            DriveRound r3 = driveRoundRepository.save(DriveRound.builder()
                    .drive(drive)
                    .roundType(RoundType.MANAGERIAL)
                    .orderNo(3)
                    .roundDate(today.plusDays(i * 2 + 3))
                    .venue("Conference Room 2")
                    .durationMins(30)
                    .cutoff(60.0)
                    .instructions("Behavioral questions, project walkthrough, and cultural fit.")
                    .build());

            // Seed sample results for students in Round 1
            for (int s = 0; s < Math.min(8, students.size()); s++) {
                Student student = students.get(s);
                double score = 65.0 + (s * 4.5);
                RoundResultStatus status = score >= 70.0 ? RoundResultStatus.PASSED : RoundResultStatus.FAILED;

                roundResultRepository.save(RoundResult.builder()
                        .round(r1)
                        .student(student)
                        .score(score)
                        .result(status)
                        .remarks(status == RoundResultStatus.PASSED ? "Strong in quant" : "Below cutoff")
                        .build());

                // If passed R1, add result in R2
                if (status == RoundResultStatus.PASSED) {
                    double r2Score = 70.0 + (s * 3.0);
                    RoundResultStatus r2Status = r2Score >= 75.0 ? RoundResultStatus.PASSED : RoundResultStatus.FAILED;

                    roundResultRepository.save(RoundResult.builder()
                            .round(r2)
                            .student(student)
                            .score(r2Score)
                            .result(r2Status)
                            .remarks(r2Status == RoundResultStatus.PASSED ? "Good problem solver" : "Need DSA practice")
                            .build());

                    if (r2Status == RoundResultStatus.PASSED) {
                        roundResultRepository.save(RoundResult.builder()
                                .round(r3)
                                .student(student)
                                .score(85.0)
                                .result(RoundResultStatus.PASSED)
                                .remarks("Recommended for offer")
                                .build());
                    }
                }
            }
        }

        log.info("Seeded drive rounds and results successfully.");
    }
}
