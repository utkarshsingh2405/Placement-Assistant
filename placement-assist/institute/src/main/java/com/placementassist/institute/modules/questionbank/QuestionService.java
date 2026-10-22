package com.placementassist.institute.modules.questionbank;

import com.placementassist.institute.common.enums.QuestionDifficulty;
import com.placementassist.institute.modules.companydrive.Drive;
import com.placementassist.institute.modules.companydrive.DriveRepository;
import com.placementassist.institute.modules.questionbank.dto.CreateQuestionRequest;
import com.placementassist.institute.modules.questionbank.dto.QuestionResponse;
import com.placementassist.institute.modules.questionbank.dto.UpdateQuestionRequest;
import com.placementassist.shared.api.PageResponse;
import com.placementassist.shared.exception.ApiException;
import com.placementassist.shared.exception.NotFoundException;
import com.placementassist.shared.util.CsvUtil;
import com.placementassist.shared.util.SpecificationBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Service managing interview question bank and student practice mode.
 */
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final DriveRepository driveRepository;
    private final QuestionMapper mapper;

    public QuestionService(
            QuestionRepository questionRepository,
            DriveRepository driveRepository,
            QuestionMapper mapper) {
        this.questionRepository = questionRepository;
        this.driveRepository = driveRepository;
        this.mapper = mapper;
    }

    @Transactional
    public QuestionResponse createQuestion(CreateQuestionRequest request) {
        Question question = mapper.toEntity(request);
        if (request.getDriveId() != null) {
            Drive drive = driveRepository.findById(request.getDriveId())
                    .orElseThrow(() -> new NotFoundException("Drive", request.getDriveId()));
            question.setDrive(drive);
        }
        question.setTagsList(request.getTags());

        question = questionRepository.save(question);
        return mapper.toResponse(question);
    }

    @Transactional
    public QuestionResponse updateQuestion(Long id, UpdateQuestionRequest request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Question", id));

        mapper.updateEntityFromDto(request, question);

        if (request.getDriveId() != null) {
            Drive drive = driveRepository.findById(request.getDriveId())
                    .orElseThrow(() -> new NotFoundException("Drive", request.getDriveId()));
            question.setDrive(drive);
        }
        if (request.getTags() != null) {
            question.setTagsList(request.getTags());
        }

        question = questionRepository.save(question);
        return mapper.toResponse(question);
    }

    @Transactional(readOnly = true)
    public QuestionResponse getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Question", id));
        return mapper.toResponse(question);
    }

    @Transactional(readOnly = true)
    public PageResponse<QuestionResponse> listQuestions(
            String search,
            Long driveId,
            String role,
            String roundType,
            QuestionDifficulty difficulty,
            Pageable pageable) {

        Specification<Question> spec = SpecificationBuilder.<Question>create()
                .withSearch(search, "question", "answer", "role", "tags")
                .withEqual("drive.id", driveId)
                .withEqualIgnoreCase("role", role)
                .withEqualIgnoreCase("roundType", roundType)
                .withEqual("difficulty", difficulty)
                .build();

        Page<Question> page = questionRepository.findAll(spec, pageable);
        return PageResponse.from(page.map(mapper::toResponse));
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> getRandomQuestions(int count, QuestionDifficulty difficulty, String role) {
        String diffStr = difficulty != null ? difficulty.name() : null;
        String roleStr = (role != null && !role.isBlank()) ? role.trim() : null;
        int limit = Math.max(1, Math.min(count, 50));

        List<Question> list = questionRepository.findRandomQuestions(diffStr, roleStr, limit);
        return list.stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public int bulkImportQuestionsCsv(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "EMPTY_FILE", "Uploaded CSV file is empty");
        }

        try (InputStream is = file.getInputStream()) {
            List<String[]> rows = CsvUtil.readCsv(is);
            if (rows.isEmpty()) return 0;

            int startIdx = 0;
            if (rows.get(0).length > 0 && rows.get(0)[0].toLowerCase().contains("question")) {
                startIdx = 1; // Skip header
            }

            int count = 0;
            List<Question> batch = new ArrayList<>();

            for (int i = startIdx; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length < 2) continue;

                String questionText = row[0].trim();
                String answerText = row[1].trim();
                String role = row.length > 2 ? row[2].trim() : null;
                String roundType = row.length > 3 ? row[3].trim() : null;

                QuestionDifficulty difficulty = QuestionDifficulty.MEDIUM;
                if (row.length > 4 && row[4] != null && !row[4].isBlank()) {
                    try {
                        difficulty = QuestionDifficulty.valueOf(row[4].trim().toUpperCase());
                    } catch (Exception ignored) {}
                }

                String tags = row.length > 5 ? row[5].trim() : null;
                Integer year = null;
                if (row.length > 6 && row[6] != null && !row[6].isBlank()) {
                    try {
                        year = Integer.parseInt(row[6].trim());
                    } catch (Exception ignored) {}
                }

                Question q = Question.builder()
                        .question(questionText)
                        .answer(answerText)
                        .role(role)
                        .roundType(roundType)
                        .difficulty(difficulty)
                        .tags(tags)
                        .askedYear(year)
                        .build();

                batch.add(q);
                count++;
            }

            questionRepository.saveAll(batch);
            return count;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "CSV_IMPORT_FAILED", "Failed to import questions: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Question", id));
        questionRepository.delete(question);
    }
}
