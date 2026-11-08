package com.placementassist.institute.modules.questionbank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementassist.institute.common.enums.QuestionDifficulty;
import com.placementassist.institute.modules.auth.UserRepository;
import com.placementassist.institute.modules.questionbank.dto.CreateQuestionRequest;
import com.placementassist.institute.modules.questionbank.dto.QuestionResponse;
import com.placementassist.shared.security.JwtAuthFilter;
import com.placementassist.shared.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuestionController.class)
@AutoConfigureMockMvc(addFilters = false)
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuestionService service;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @DisplayName("GET /api/questions/random - Returns questions for practice mode")
    void testGetRandomQuestions() throws Exception {
        QuestionResponse q = QuestionResponse.builder()
                .id(1L)
                .question("What is ACID in DBMS?")
                .answer("Atomicity, Consistency, Isolation, Durability.")
                .difficulty(QuestionDifficulty.EASY)
                .role("SDE")
                .build();

        when(service.getRandomQuestions(5, QuestionDifficulty.EASY, "SDE")).thenReturn(List.of(q));

        mockMvc.perform(get("/api/questions/random")
                        .param("count", "5")
                        .param("difficulty", "EASY")
                        .param("role", "SDE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].question").value("What is ACID in DBMS?"));
    }

    @Test
    @DisplayName("POST /api/questions - Creates a new question")
    @WithMockUser(roles = "TPO")
    void testCreateQuestion() throws Exception {
        CreateQuestionRequest request = CreateQuestionRequest.builder()
                .question("Explain Java GC")
                .answer("Garbage collector reclaims memory.")
                .difficulty(QuestionDifficulty.MEDIUM)
                .role("SDE")
                .build();

        QuestionResponse response = QuestionResponse.builder()
                .id(1L)
                .question("Explain Java GC")
                .answer("Garbage collector reclaims memory.")
                .difficulty(QuestionDifficulty.MEDIUM)
                .build();

        when(service.createQuestion(any(CreateQuestionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.question").value("Explain Java GC"));
    }
}
