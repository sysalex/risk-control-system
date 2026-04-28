package com.harness.risk.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.BaseApiIntegrationTest;
import com.harness.risk.application.dto.AuditLogResponse;
import com.harness.risk.application.service.AuditLogService;
import com.harness.risk.common.exception.AppException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link AuditLogController} Web MVC 测试。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
class AuditLogControllerTest extends BaseApiIntegrationTest {

    @MockBean
    private AuditLogService auditLogService;

    private String adminToken() {
        return adminToken(1L, "alice");
    }

    private String analystToken() {
        return analystToken(2L, "bob");
    }

    private AuditLogResponse sampleAuditLog() {
        return new AuditLogResponse(
                1L, 2L, "create", "rule", 3L,
                null, "{\"id\":3}", LocalDateTime.now(), "127.0.0.1");
    }

    @Test
    void listReturnsPagedAuditLogsForAdmin() throws Exception {
        Page<AuditLogResponse> page = new Page<>();
        page.setRecords(List.of(sampleAuditLog()));
        page.setTotal(1);
        when(auditLogService.list(1, 20, 2L, "create", "rule")).thenReturn(page);

        mockMvc.perform(get("/api/v1/audit-logs?page=1&limit=20&userId=2&action=create&resourceType=rule")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].resourceType").value("rule"));
    }

    @Test
    void listForbiddenForAnalyst() throws Exception {
        mockMvc.perform(get("/api/v1/audit-logs")
                        .header("Authorization", analystToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getByIdReturnsAuditLogForAdmin() throws Exception {
        when(auditLogService.getById(1L)).thenReturn(sampleAuditLog());

        mockMvc.perform(get("/api/v1/audit-logs/1")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.action").value("create"));
    }

    @Test
    void getByIdReturnsNotFound() throws Exception {
        when(auditLogService.getById(99L)).thenThrow(AppException.notFound("AuditLog"));

        mockMvc.perform(get("/api/v1/audit-logs/99")
                        .header("Authorization", adminToken()))
                .andExpect(status().isNotFound());
    }
}
