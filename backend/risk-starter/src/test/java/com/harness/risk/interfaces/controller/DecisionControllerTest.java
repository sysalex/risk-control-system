package com.harness.risk.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.BaseApiIntegrationTest;
import com.harness.risk.application.dto.CreateDecisionRequest;
import com.harness.risk.application.dto.DecisionResponse;
import com.harness.risk.application.dto.UpdateDecisionRequest;
import com.harness.risk.application.service.AuditLogService;
import com.harness.risk.application.service.DecisionService;
import com.harness.risk.domain.enums.DecisionTypeEnums;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link DecisionController} Web MVC 测试
 *
 * @author harness-agent
 * @since 2026-04-28
 */
class DecisionControllerTest extends BaseApiIntegrationTest {

    @MockBean
    private DecisionService decisionService;
    @MockBean
    private AuditLogService auditLogService;

    private String adminToken() {
        return adminToken(1L, "alice");
    }

    private String analystToken() {
        return analystToken(2L, "bob");
    }

    private DecisionResponse sampleDecision() {
        return new DecisionResponse(
                1L, 10L, DecisionTypeEnums.MANUAL_REVIEW, "风险评分过高",
                "需要人工复核", 1L, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void listReturnsPagedDecisions() throws Exception {
        Page<DecisionResponse> page = new Page<>();
        page.setRecords(List.of(sampleDecision()));
        page.setTotal(1);
        when(decisionService.list(1, 20)).thenReturn(page);

        mockMvc.perform(get("/api/v1/decisions?page=1&limit=20")
                        .header("Authorization", analystToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].reason").value("风险评分过高"));
    }

    @Test
    void createReturnsDecisionResponse() throws Exception {
        when(decisionService.create(any())).thenReturn(sampleDecision());

        mockMvc.perform(post("/api/v1/decisions")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateDecisionRequest(
                                10L, DecisionTypeEnums.MANUAL_REVIEW, "风险评分过高", "需要人工复核", 1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.decisionType").value("MANUAL_REVIEW"));
    }

    @Test
    void createForbiddenForAnalyst() throws Exception {
        mockMvc.perform(post("/api/v1/decisions")
                        .header("Authorization", analystToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateDecisionRequest(
                                10L, DecisionTypeEnums.MANUAL_REVIEW, "风险评分过高", "需要人工复核", 1L))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getByIdReturnsDecision() throws Exception {
        when(decisionService.getById(1L)).thenReturn(sampleDecision());

        mockMvc.perform(get("/api/v1/decisions/1")
                        .header("Authorization", analystToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.eventId").value(10));
    }

    @Test
    void updateReturnsDecisionResponse() throws Exception {
        when(decisionService.update(eq(1L), any())).thenReturn(sampleDecision());

        mockMvc.perform(put("/api/v1/decisions/1")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateDecisionRequest(
                                DecisionTypeEnums.ESCALATE, "升级处理", "转高级审核"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reason").value("风险评分过高"));
    }
}
