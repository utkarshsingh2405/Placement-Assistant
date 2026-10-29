package com.placementassist.institute.modules.driveround;

import com.placementassist.institute.common.enums.RoundResultStatus;
import com.placementassist.institute.modules.companydrive.Drive;
import com.placementassist.institute.modules.companydrive.DriveRepository;
import com.placementassist.institute.modules.driveround.dto.*;
import com.placementassist.institute.modules.student.Student;
import com.placementassist.institute.modules.student.StudentRepository;
import com.placementassist.shared.exception.ApiException;
import com.placementassist.shared.exception.NotFoundException;
import com.placementassist.shared.util.CsvUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service managing evaluation rounds in recruitment drives, candidate evaluations, and funnel statistics.
 */
@Service
public class DriveRoundService {

    private final DriveRoundRepository driveRoundRepository;
    private final RoundResultRepository roundResultRepository;
    private final DriveRepository driveRepository;
    private final StudentRepository studentRepository;
    private final DriveRoundMapper mapper;

    public DriveRoundService(
            DriveRoundRepository driveRoundRepository,
            RoundResultRepository roundResultRepository,
            DriveRepository driveRepository,
            StudentRepository studentRepository,
            DriveRoundMapper mapper) {
        this.driveRoundRepository = driveRoundRepository;
        this.roundResultRepository = roundResultRepository;
        this.driveRepository = driveRepository;
        this.studentRepository = studentRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<DriveRoundResponse> getRoundsForDrive(Long driveId) {
        if (!driveRepository.existsById(driveId)) {
            throw new NotFoundException("Drive", driveId);
        }
        return driveRoundRepository.findByDriveIdOrderByOrderNoAsc(driveId).stream()
                .map(mapper::toRoundResponse)
                .toList();
    }

    @Transactional
    public DriveRoundResponse createRound(Long driveId, CreateRoundRequest request) {
        Drive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new NotFoundException("Drive", driveId));

        DriveRound round = mapper.toRoundEntity(request);
        round.setDrive(drive);

        round = driveRoundRepository.save(round);
        return mapper.toRoundResponse(round);
    }

    @Transactional
    public DriveRoundResponse updateRound(Long roundId, UpdateRoundRequest request) {
        DriveRound round = driveRoundRepository.findById(roundId)
                .orElseThrow(() -> new NotFoundException("DriveRound", roundId));

        mapper.updateRoundFromDto(request, round);
        round = driveRoundRepository.save(round);
        return mapper.toRoundResponse(round);
    }

    @Transactional
    public void deleteRound(Long roundId) {
        DriveRound round = driveRoundRepository.findById(roundId)
                .orElseThrow(() -> new NotFoundException("DriveRound", roundId));
        driveRoundRepository.delete(round);
    }

    // --- ROUND RESULTS ---

    @Transactional(readOnly = true)
    public List<RoundResultResponse> getResultsForRound(Long roundId) {
        if (!driveRoundRepository.existsById(roundId)) {
            throw new NotFoundException("DriveRound", roundId);
        }
        return roundResultRepository.findByRoundId(roundId).stream()
                .map(mapper::toResultResponse)
                .toList();
    }

    @Transactional
    public RoundResultResponse submitResult(Long roundId, SubmitRoundResultRequest request) {
        DriveRound round = driveRoundRepository.findById(roundId)
                .orElseThrow(() -> new NotFoundException("DriveRound", roundId));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new NotFoundException("Student", request.getStudentId()));

        RoundResult result = roundResultRepository.findByRoundIdAndStudentId(roundId, student.getId())
                .orElseGet(() -> RoundResult.builder()
                        .round(round)
                        .student(student)
                        .build());

        result.setScore(request.getScore());
        result.setResult(request.getResult());
        result.setRemarks(request.getRemarks());

        result = roundResultRepository.save(result);
        return mapper.toResultResponse(result);
    }

    /**
     * Bulk uploads candidate round scores and outcomes from a CSV spreadsheet.
     * CSV columns expected: RollNo, Score, Result, Remarks
     */
    @Transactional
    public int bulkUploadResultsCsv(Long roundId, MultipartFile file) {
        DriveRound round = driveRoundRepository.findById(roundId)
                .orElseThrow(() -> new NotFoundException("DriveRound", roundId));

        if (file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "EMPTY_FILE", "Uploaded CSV file is empty");
        }

        try (InputStream is = file.getInputStream()) {
            List<String[]> rows = CsvUtil.readCsv(is);
            if (rows.isEmpty()) {
                return 0;
            }

            int processedCount = 0;
            // Check for header row
            int startIdx = 0;
            if (rows.get(0).length > 0 && rows.get(0)[0].toLowerCase().contains("roll")) {
                startIdx = 1;
            }

            for (int i = startIdx; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length < 3) continue;

                String rollNo = row[0].trim();
                Optional<Student> studentOpt = studentRepository.findByRollNo(rollNo);
                if (studentOpt.isEmpty()) continue;

                Student student = studentOpt.get();
                Double score = null;
                try {
                    score = Double.parseDouble(row[1].trim());
                } catch (Exception ignored) {}

                RoundResultStatus status = RoundResultStatus.PENDING;
                try {
                    status = RoundResultStatus.valueOf(row[2].trim().toUpperCase());
                } catch (Exception ignored) {}

                String remarks = row.length > 3 ? row[3].trim() : null;

                RoundResult result = roundResultRepository.findByRoundIdAndStudentId(roundId, student.getId())
                        .orElseGet(() -> RoundResult.builder()
                                .round(round)
                                .student(student)
                                .build());

                result.setScore(score);
                result.setResult(status);
                result.setRemarks(remarks);

                roundResultRepository.save(result);
                processedCount++;
            }

            return processedCount;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "CSV_PROCESSING_FAILED", "Failed to process results CSV: " + e.getMessage());
        }
    }

    /**
     * Calculates candidate progression funnel across all ordered rounds of a drive.
     */
    @Transactional(readOnly = true)
    public DriveFunnelResponse getDriveFunnel(Long driveId) {
        Drive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new NotFoundException("Drive", driveId));

        List<DriveRound> rounds = driveRoundRepository.findByDriveIdOrderByOrderNoAsc(driveId);
        List<DriveFunnelResponse.FunnelStage> stages = new ArrayList<>();

        for (DriveRound r : rounds) {
            long passed = roundResultRepository.countByRoundIdAndResult(r.getId(), RoundResultStatus.PASSED);
            stages.add(DriveFunnelResponse.FunnelStage.builder()
                    .orderNo(r.getOrderNo())
                    .roundType(r.getRoundType())
                    .passedCount(passed)
                    .build());
        }

        return DriveFunnelResponse.builder()
                .driveId(drive.getId())
                .driveTitle(drive.getTitle())
                .totalEligibleStudents(studentRepository.count())
                .funnel(stages)
                .build();
    }
}
