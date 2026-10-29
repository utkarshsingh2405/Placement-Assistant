package com.placementassist.institute.modules.questionbank;

import com.placementassist.institute.common.enums.QuestionDifficulty;
import com.placementassist.institute.modules.questionbank.dto.CreateQuestionRequest;
import com.placementassist.institute.modules.questionbank.dto.QuestionResponse;
import com.placementassist.institute.modules.questionbank.dto.UpdateQuestionRequest;
import com.placementassist.shared.api.ApiResponse;
import com.placementassist.shared.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@Tag(name = "Question Bank", description = "Interview questions repository, category search, practice mode, and CSV import")
public class QuestionController {

    private final QuestionService service;

    public QuestionController(QuestionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Search question bank with filters and pagination")
    public ResponseEntity<ApiResponse<PageResponse<QuestionResponse>>> listQuestions(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long driveId,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String roundType,
            @RequestParam(required = false) QuestionDifficulty difficulty,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<QuestionResponse> response = service.listQuestions(search, driveId, role, roundType, difficulty, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/random")
    @Operation(summary = "Get random questions for interview practice mode")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getRandomQuestions(
            @RequestParam(defaultValue = "5") int count,
            @RequestParam(required = false) QuestionDifficulty difficulty,
            @RequestParam(required = false) String role) {
        List<QuestionResponse> response = service.getRandomQuestions(count, difficulty, role);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get question by ID")
    public ResponseEntity<ApiResponse<QuestionResponse>> getQuestionById(@PathVariable Long id) {
        QuestionResponse response = service.getQuestionById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Add a new interview question")
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
        QuestionResponse response = service.createQuestion(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Question created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Update an existing question")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody UpdateQuestionRequest request) {
        QuestionResponse response = service.updateQuestion(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Question updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Delete question from bank")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long id) {
        service.deleteQuestion(id);
        return ResponseEntity.ok(ApiResponse.message("Question deleted successfully"));
    }

    @PostMapping(value = "/bulk-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TPO', 'DIRECTOR', 'ADMIN')")
    @Operation(summary = "Bulk import interview questions from CSV (Question, Answer, Role, RoundType, Difficulty, Tags, AskedYear)")
    public ResponseEntity<ApiResponse<String>> bulkImportQuestions(@RequestParam("file") MultipartFile file) {
        int count = service.bulkImportQuestionsCsv(file);
        return ResponseEntity.ok(ApiResponse.ok(
                String.format("Successfully imported %d questions from CSV", count),
                String.format("%d records created", count)
        ));
    }
}
