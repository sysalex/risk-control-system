package com.harness.risk.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.BaseApiIntegrationTest;
import com.harness.risk.application.dto.CreateEventRequest;
import com.harness.risk.application.dto.EventResponse;
import com.harness.risk.application.dto.UpdateEventRequest;
import com.harness.risk.application.service.AuditLogService;
import com.harness.risk.application.service.RiskEventService;
import com.harness.risk.domain.enums.RiskEventStatusEnums;
import com.harness.risk.domain.enums.RiskLevelEnums;
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
 * {@link EventController} Web MVC 测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class EventControllerTest extends BaseApiIntegrationTest {

    @MockBean
    private RiskEventService riskEventService;
    @MockBean
    private AuditLogService auditLogService;

    private String adminToken() {
        return adminToken(1L, "alice");
    }

    private String analystToken() {
        return analystToken(2L, "bob");
    }

    private EventResponse sampleEvent() {
        return new EventResponse(
                1L, 10L, "user", "user-123",
                RiskLevelEnums.HIGH, RiskEventStatusEnums.PENDING,
                "大额交易异常", LocalDateTime.now(),
                null, null, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void listReturnsPagedEvents() throws Exception {
        Page<EventResponse> page = new Page<>();
        page.setRecords(List.of(sampleEvent()));
        page.setTotal(1);
        when(riskEventService.list(1, 20, null, null)).thenReturn(page);

        mockMvc.perform(get("/api/v1/events?page=1&limit=20")
                        .header("Authorization", analystToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].description").value("大额交易异常"));
    }

    @Test
    void listWithFiltersReturnsFilteredEvents() throws Exception {
        Page<EventResponse> page = new Page<>();
        page.setRecords(List.of(sampleEvent()));
        page.setTotal(1);
        when(riskEventService.list(1, 20, RiskLevelEnums.HIGH, RiskEventStatusEnums.PENDING)).thenReturn(page);

        mockMvc.perform(get("/api/v1/events?page=1&limit=20&riskLevel=HIGH&status=PENDING")
                        .header("Authorization", analystToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].riskLevel").value("HIGH"));
    }

    @Test
    void getByIdReturnsEvent() throws Exception {
        when(riskEventService.getById(1L)).thenReturn(sampleEvent());

        mockMvc.perform(get("/api/v1/events/1")
                        .header("Authorization", analystToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.description").value("大额交易异常"));
    }

    @Test
    void createReturnsEventResponse() throws Exception {
        when(riskEventService.create(any())).thenReturn(sampleEvent());

        mockMvc.perform(post("/api/v1/events")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateEventRequest(10L, "user", "user-123", RiskLevelEnums.HIGH, "大额交易异常"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.riskLevel").value("HIGH"));
    }

    @Test
    void createForbiddenForAnalyst() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .header("Authorization", analystToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateEventRequest(10L, "user", "user-123", RiskLevelEnums.HIGH, "大额交易异常"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateReturnsUpdatedEvent() throws Exception {
        when(riskEventService.update(eq(1L), any())).thenReturn(sampleEvent());

        mockMvc.perform(put("/api/v1/events/1")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateEventRequest(RiskEventStatusEnums.INVESTIGATING, "补充说明"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.description").value("大额交易异常"));
    }

    @Test
    void resolveEventReturnsResolvedEvent() throws Exception {
        EventResponse resolved = new EventResponse(
                1L, 10L, "user", "user-123",
                RiskLevelEnums.HIGH, RiskEventStatusEnums.RESOLVED,
                "大额交易异常", LocalDateTime.now(),
                LocalDateTime.now(), 1L, LocalDateTime.now(), LocalDateTime.now());
        when(riskEventService.resolveEvent(1L, 1L)).thenReturn(resolved);

        mockMvc.perform(post("/api/v1/events/1/resolve")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("RESOLVED"))
                .andExpect(jsonPath("$.data.resolvedBy").value(1));
    }
}
