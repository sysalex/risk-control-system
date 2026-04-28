package com.harness.risk.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harness.risk.application.dto.CreateRuleRequest;
import com.harness.risk.application.dto.RuleResponse;
import com.harness.risk.application.dto.UpdateRuleRequest;
import com.harness.risk.application.service.AuditLogService;
import com.harness.risk.application.service.RiskRuleService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link RuleController} Web MVC 测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@SpringBootTest(classes = RiskApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RuleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @MockBean
    private RiskRuleService riskRuleService;
    @MockBean
    private AuditLogService auditLogService;

    private String adminToken() {
        return "Bearer " + jwtUtil.generateAccessToken(1L, "alice", "admin");
    }

    private String analystToken() {
        return "Bearer " + jwtUtil.generateAccessToken(2L, "bob", "analyst");
    }

    private RuleResponse sampleRule() {
        return new RuleResponse(
                1L, "大额交易检测", "检测单笔超过 1 万的交易",
                "{}", "{}", 10, true, 1L,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void listReturnsPagedRules() throws Exception {
        Page<RuleResponse> page = new Page<>();
        page.setRecords(List.of(sampleRule()));
        page.setTotal(1);
        when(riskRuleService.list(1, 20)).thenReturn(page);

        mockMvc.perform(get("/api/v1/rules?page=1&limit=20")
                        .header("Authorization", analystToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].name").value("大额交易检测"));
    }

    @Test
    void getByIdReturnsRule() throws Exception {
        when(riskRuleService.getById(1L)).thenReturn(sampleRule());

        mockMvc.perform(get("/api/v1/rules/1")
                        .header("Authorization", analystToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("大额交易检测"));
    }

    @Test
    void getByIdReturnsNotFound() throws Exception {
        when(riskRuleService.getById(99L)).thenThrow(
                new com.harness.risk.common.exception.AppException(404, "Rule not found"));

        mockMvc.perform(get("/api/v1/rules/99")
                        .header("Authorization", analystToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReturnsRuleResponse() throws Exception {
        when(riskRuleService.create(any(), eq(1L))).thenReturn(sampleRule());

        mockMvc.perform(post("/api/v1/rules")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateRuleRequest("大额交易检测", "描述", "{}", "{}", 10))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("大额交易检测"));
    }

    @Test
    void createForbiddenForAnalyst() throws Exception {
        mockMvc.perform(post("/api/v1/rules")
                        .header("Authorization", analystToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateRuleRequest("大额交易检测", "描述", "{}", "{}", 10))))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateReturnsUpdatedRule() throws Exception {
        when(riskRuleService.update(eq(1L), any())).thenReturn(sampleRule());

        mockMvc.perform(put("/api/v1/rules/1")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateRuleRequest("新名称", "新描述", null, null, 5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("大额交易检测"));
    }

    @Test
    void deleteReturnsSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/rules/1")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void enableRuleReturnsEnabledRule() throws Exception {
        when(riskRuleService.enableRule(1L)).thenReturn(sampleRule());

        mockMvc.perform(post("/api/v1/rules/1/enable")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enabled").value(true));
    }

    @Test
    void disableRuleReturnsDisabledRule() throws Exception {
        RuleResponse disabled = new RuleResponse(
                1L, "大额交易检测", "检测单笔超过 1 万的交易",
                "{}", "{}", 10, false, 1L,
                LocalDateTime.now(), LocalDateTime.now());
        when(riskRuleService.disableRule(1L)).thenReturn(disabled);

        mockMvc.perform(post("/api/v1/rules/1/disable")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enabled").value(false));
    }
}
