package com.harness.risk.interfaces.controller;

import com.harness.risk.BaseApiIntegrationTest;
import com.harness.risk.application.dto.*;
import com.harness.risk.application.service.*;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.domain.enums.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 端到端 API 链路测试，覆盖多 Controller 协作场景和权限边界。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
class EndToEndApiTest extends BaseApiIntegrationTest {

    @MockBean
    private AuthService authService;
    @MockBean
    private UserService userService;
    @MockBean
    private RiskRuleService riskRuleService;
    @MockBean
    private RiskEventService riskEventService;
    @MockBean
    private RiskScoreService riskScoreService;
    @MockBean
    private AuditLogService auditLogService;

    @Test
    void loginThenAccessMeAndRuleList() throws Exception {
        when(authService.login(any())).thenReturn(new TokenResponse("access-token", "refresh-token", 1800));
        when(userService.me(1L)).thenReturn(new UserResponse(1L, "alice", "alice@test.com", UserRoleEnums.ADMIN, true, LocalDateTime.now()));

        // 1. 登录
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new LoginRequest("alice", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));

        // 2. 获取当前用户
        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", adminToken(1L, "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("alice"));
    }

    @Test
    void unauthenticatedAccessReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/rules"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void analystAccessAdminOnlyEndpointsReturns403() throws Exception {
        mockMvc.perform(post("/api/v1/rules")
                        .header("Authorization", analystToken(2L, "bob"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CreateRuleRequest("test", "desc", "{}", "{}", 1))))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/events")
                        .header("Authorization", analystToken(2L, "bob"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CreateEventRequest(1L, "user", "u1", RiskLevelEnums.HIGH, "desc"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void ruleCrudLifecycle() throws Exception {
        RuleResponse rule = new RuleResponse(1L, "规则", "描述", "{}", "{}", 10, true, 1L, LocalDateTime.now(), LocalDateTime.now());
        when(riskRuleService.create(any(), eq(1L))).thenReturn(rule);
        when(riskRuleService.getById(1L)).thenReturn(rule);
        when(riskRuleService.update(eq(1L), any())).thenReturn(rule);
        when(riskRuleService.enableRule(1L)).thenReturn(rule);
        when(riskRuleService.disableRule(1L)).thenReturn(
                new RuleResponse(1L, "规则", "描述", "{}", "{}", 10, false, 1L, LocalDateTime.now(), LocalDateTime.now()));

        String token = adminToken(1L, "alice");

        // create
        mockMvc.perform(post("/api/v1/rules")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CreateRuleRequest("规则", "描述", "{}", "{}", 10))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enabled").value(true));

        // get
        mockMvc.perform(get("/api/v1/rules/1")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("规则"));

        // update
        mockMvc.perform(put("/api/v1/rules/1")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new UpdateRuleRequest("新规则", null, null, null, 5))))
                .andExpect(status().isOk());

        // disable
        mockMvc.perform(post("/api/v1/rules/1/disable")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enabled").value(false));

        // enable
        mockMvc.perform(post("/api/v1/rules/1/enable")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enabled").value(true));

        // delete
        mockMvc.perform(delete("/api/v1/rules/1")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void eventCreateQueryResolveFlow() throws Exception {
        EventResponse event = new EventResponse(
                1L, 10L, "user", "u1", RiskLevelEnums.HIGH, RiskEventStatusEnums.PENDING,
                "异常", LocalDateTime.now(), null, null, LocalDateTime.now(), LocalDateTime.now());
        EventResponse resolved = new EventResponse(
                1L, 10L, "user", "u1", RiskLevelEnums.HIGH, RiskEventStatusEnums.RESOLVED,
                "异常", LocalDateTime.now(), LocalDateTime.now(), 1L, LocalDateTime.now(), LocalDateTime.now());

        when(riskEventService.create(any())).thenReturn(event);
        when(riskEventService.getById(1L)).thenReturn(event);
        when(riskEventService.resolveEvent(1L, 1L)).thenReturn(resolved);

        String token = adminToken(1L, "alice");

        // create
        mockMvc.perform(post("/api/v1/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CreateEventRequest(10L, "user", "u1", RiskLevelEnums.HIGH, "异常"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        // get
        mockMvc.perform(get("/api/v1/events/1")
                        .header("Authorization", analystToken(2L, "bob")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.riskLevel").value("HIGH"));

        // resolve
        mockMvc.perform(post("/api/v1/events/1/resolve")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("RESOLVED"))
                .andExpect(jsonPath("$.data.resolvedBy").value(1));
    }

    @Test
    void operatorCannotAccessAuditLogs() throws Exception {
        mockMvc.perform(get("/api/v1/audit-logs")
                        .header("Authorization", operatorToken(3L, "charlie")))
                .andExpect(status().isForbidden());
    }

    @Test
    void notFoundReturns404() throws Exception {
        when(riskRuleService.getById(99L)).thenThrow(AppException.notFound("Rule"));

        mockMvc.perform(get("/api/v1/rules/99")
                        .header("Authorization", analystToken(2L, "bob")))
                .andExpect(status().isNotFound());
    }

    @Test
    void scoreEvaluateAndQuerySubject() throws Exception {
        ScoreResponse score = new ScoreResponse(
                1L, 10L, "user", "u1", new BigDecimal("86.5"), new BigDecimal("100"),
                "{}", LocalDateTime.now(), 1L, LocalDateTime.now(), LocalDateTime.now());

        when(riskScoreService.create(any())).thenReturn(score);
        when(riskScoreService.getLatestBySubject("user", "u1")).thenReturn(score);

        String admin = adminToken(1L, "alice");

        mockMvc.perform(post("/api/v1/scores/evaluate")
                        .header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CreateScoreRequest(10L, "user", "u1", new BigDecimal("86.5"), new BigDecimal("100"), "{}", 1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subjectId").value("u1"));

        mockMvc.perform(get("/api/v1/scores/subject/user/u1")
                        .header("Authorization", analystToken(2L, "bob")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.score").value(86.5));
    }
}
