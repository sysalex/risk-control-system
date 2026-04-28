package com.harness.risk.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harness.risk.application.dto.CreateScoreRequest;
import com.harness.risk.application.dto.ScoreResponse;
import com.harness.risk.application.service.RiskScoreService;
import com.harness.risk.common.security.JwtUtil;
import com.harness.risk.starter.RiskApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link ScoreController} Web MVC 测试
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@SpringBootTest(classes = RiskApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @MockBean
    private RiskScoreService riskScoreService;

    private String adminToken() {
        return "Bearer " + jwtUtil.generateAccessToken(1L, "alice", "admin");
    }

    private String analystToken() {
        return "Bearer " + jwtUtil.generateAccessToken(2L, "bob", "analyst");
    }

    private ScoreResponse sampleScore() {
        return new ScoreResponse(
                1L, 10L, "user", "user-123",
                new BigDecimal("86.50"), new BigDecimal("100.00"), "{\"behavior\":40}",
                LocalDateTime.now(), 1L, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void listReturnsPagedScores() throws Exception {
        Page<ScoreResponse> page = new Page<>();
        page.setRecords(List.of(sampleScore()));
        page.setTotal(1);
        when(riskScoreService.list(1, 20)).thenReturn(page);

        mockMvc.perform(get("/api/v1/scores?page=1&limit=20")
                        .header("Authorization", analystToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].subjectId").value("user-123"));
    }

    @Test
    void createReturnsScoreResponse() throws Exception {
        when(riskScoreService.create(any())).thenReturn(sampleScore());

        mockMvc.perform(post("/api/v1/scores/evaluate")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateScoreRequest(
                                10L, "user", "user-123", new BigDecimal("86.50"),
                                new BigDecimal("100.00"), "{\"behavior\":40}", 1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.score").value(86.50));
    }

    @Test
    void createForbiddenForAnalyst() throws Exception {
        mockMvc.perform(post("/api/v1/scores/evaluate")
                        .header("Authorization", analystToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateScoreRequest(
                                10L, "user", "user-123", new BigDecimal("86.50"),
                                new BigDecimal("100.00"), "{\"behavior\":40}", 1L))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getByIdReturnsScore() throws Exception {
        when(riskScoreService.getById(1L)).thenReturn(sampleScore());

        mockMvc.perform(get("/api/v1/scores/1")
                        .header("Authorization", analystToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.eventId").value(10));
    }

    @Test
    void getLatestBySubjectReturnsScore() throws Exception {
        when(riskScoreService.getLatestBySubject("user", "user-123")).thenReturn(sampleScore());

        mockMvc.perform(get("/api/v1/scores/subject/user/user-123")
                        .header("Authorization", analystToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subjectId").value("user-123"));
    }
}
