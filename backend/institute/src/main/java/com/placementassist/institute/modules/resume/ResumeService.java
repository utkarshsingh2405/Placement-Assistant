package com.placementassist.institute.modules.resume;

import com.placementassist.institute.common.enums.ResumeStatus;
import com.placementassist.institute.config.FileStorageConfig;
import com.placementassist.institute.modules.resume.dto.AtsBreakdownDto;
import com.placementassist.institute.modules.resume.dto.RescoreResumeRequest;
import com.placementassist.institute.modules.resume.dto.ResumeResponse;
import com.placementassist.institute.modules.resume.dto.ReviewResumeRequest;
import com.placementassist.institute.modules.student.Student;
import com.placementassist.institute.modules.student.StudentRepository;
import com.placementassist.shared.api.PageResponse;
import com.placementassist.shared.exception.ApiException;
import com.placementassist.shared.exception.NotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Service managing student resume upload, Apache PDFBox text extraction,
 * automated 100-point ATS evaluation, staff reviews, and re-scoring.
 */
@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final StudentRepository studentRepository;
    private final FileStorageConfig fileStorageConfig;
    private final AtsScorer atsScorer;
    private final ResumeMapper mapper;

    public ResumeService(
            ResumeRepository resumeRepository,
            StudentRepository studentRepository,
            FileStorageConfig fileStorageConfig,
            AtsScorer atsScorer,
            ResumeMapper mapper) {
        this.resumeRepository = resumeRepository;
        this.studentRepository = studentRepository;
        this.fileStorageConfig = fileStorageConfig;
        this.atsScorer = atsScorer;
        this.mapper = mapper;
    }

    @Transactional
    public ResumeResponse uploadResume(Long studentId, MultipartFile file, String targetJobDescription) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student", studentId));

        if (file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "EMPTY_FILE", "Uploaded resume PDF cannot be empty");
        }

        // 1. Store PDF file safely
        String subFolder = "student_" + student.getRollNo();
        String fileUrl = fileStorageConfig.storeFile(file, subFolder);

        // 2. Extract text with Apache PDFBox and score with 100-pt ATS Engine
        byte[] pdfBytes;
        try {
            pdfBytes = file.getBytes();
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_READ_ERROR", "Failed to read uploaded file: " + e.getMessage());
        }

        String extractedText = atsScorer.extractTextFromPdf(pdfBytes);
        AtsBreakdownDto breakdown = atsScorer.scoreResume(extractedText, targetJobDescription);
        String breakdownJson = atsScorer.toJson(breakdown);

        // 3. Increment version
        int nextVersion = resumeRepository.findTopByStudentIdOrderByVersionDesc(studentId)
                .map(r -> r.getVersion() + 1)
                .orElse(1);

        Resume resume = Resume.builder()
                .student(student)
                .fileUrl(fileUrl)
                .version(nextVersion)
                .status(ResumeStatus.PENDING)
                .atsScore(breakdown.totalScore())
                .atsBreakdown(breakdownJson)
                .feedback(null)
                .reviewedBy(null)
                .build();

        Resume saved = resumeRepository.save(resume);
        return mapper.toResponse(saved, breakdown);
    }

    @Transactional(readOnly = true)
    public ResumeResponse getResumeById(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resume", id));
        return mapper.toResponse(resume);
    }

    @Transactional(readOnly = true)
    public PageResponse<ResumeResponse> listResumes(Long studentId, ResumeStatus status, Pageable pageable) {
        Page<Resume> page;
        if (studentId != null) {
            page = resumeRepository.findByStudentId(studentId, pageable);
        } else if (status != null) {
            page = resumeRepository.findByStatus(status, pageable);
        } else {
            page = resumeRepository.findAll(pageable);
        }

        List<ResumeResponse> content = page.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponse.<ResumeResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ResumeResponse> getStudentResumes(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new NotFoundException("Student", studentId);
        }
        return resumeRepository.findByStudentIdOrderByVersionDesc(studentId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public ResumeResponse reviewResume(Long id, ReviewResumeRequest request, String reviewerName) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resume", id));

        resume.setStatus(request.status());
        resume.setFeedback(request.feedback());
        resume.setReviewedBy(reviewerName != null ? reviewerName : "TPO_STAFF");

        Resume updated = resumeRepository.save(resume);
        return mapper.toResponse(updated);
    }

    @Transactional
    public ResumeResponse rescoreResume(Long id, RescoreResumeRequest request) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resume", id));

        Path filePath = fileStorageConfig.loadFileAsPath(resume.getFileUrl());
        if (!Files.exists(filePath)) {
            throw new NotFoundException("Resume file on storage", resume.getFileUrl());
        }

        byte[] pdfBytes;
        try {
            pdfBytes = Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_READ_ERROR", "Failed to read stored resume file: " + e.getMessage());
        }

        String extractedText = atsScorer.extractTextFromPdf(pdfBytes);
        AtsBreakdownDto breakdown = atsScorer.scoreResume(extractedText, request.jobDescription());
        String breakdownJson = atsScorer.toJson(breakdown);

        resume.setAtsScore(breakdown.totalScore());
        resume.setAtsBreakdown(breakdownJson);

        Resume saved = resumeRepository.save(resume);
        return mapper.toResponse(saved, breakdown);
    }

    @Transactional(readOnly = true)
    public Resource loadResumeAsResource(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resume", id));

        Path filePath = fileStorageConfig.loadFileAsPath(resume.getFileUrl());
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new NotFoundException("Resume file", resume.getFileUrl());
            }
        } catch (MalformedURLException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_URL_ERROR", "Error constructing file URL: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Optional<Resume> getLatestApprovedResume(Long studentId) {
        return resumeRepository.findTopByStudentIdAndStatusOrderByVersionDesc(studentId, ResumeStatus.APPROVED);
    }
}
