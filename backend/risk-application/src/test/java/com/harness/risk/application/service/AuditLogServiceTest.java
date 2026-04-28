package com.harness.risk.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.AuditLogResponse;
import com.harness.risk.application.dto.CreateAuditLogRequest;
import com.harness.risk.application.service.impl.AuditLogServiceImpl;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.domain.model.entity.AuditLogEntity;
import com.harness.risk.infrastructure.mapper.AuditLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link AuditLogServiceImpl} 单元测试。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogMapper auditLogMapper;

    private AuditLogServiceImpl auditLogService;

    @BeforeEach
    void setUp() {
        auditLogService = new AuditLogServiceImpl();
        ReflectionTestUtils.setField(auditLogService, "baseMapper", auditLogMapper);
    }

    private AuditLogEntity sampleAuditLog() {
        AuditLogEntity auditLog = new AuditLogEntity();
        auditLog.setId(1L);
        auditLog.setUserId(2L);
        auditLog.setAction("create");
        auditLog.setResourceType("rule");
        auditLog.setResourceId(3L);
        auditLog.setOldValues(null);
        auditLog.setNewValues("{\"id\":3}");
        auditLog.setIpAddress("127.0.0.1");
        auditLog.setCreatedAt(LocalDateTime.now());
        return auditLog;
    }

    @Test
    void recordPersistsAuditLog() {
        AuditLogResponse response = auditLogService.record(
                new CreateAuditLogRequest(2L, "create", "rule", 3L, null, "{\"id\":3}", "127.0.0.1"));

        assertEquals("create", response.getAction());
        assertEquals("rule", response.getResourceType());
        verify(auditLogMapper).insert(any(AuditLogEntity.class));
    }

    @Test
    void getByIdReturnsAuditLog() {
        when(auditLogMapper.selectById(1L)).thenReturn(sampleAuditLog());

        AuditLogResponse response = auditLogService.getById(1L);

        assertEquals(2L, response.getUserId());
        assertEquals("127.0.0.1", response.getIpAddress());
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(auditLogMapper.selectById(1L)).thenReturn(null);

        AppException exception = assertThrows(AppException.class, () -> auditLogService.getById(1L));

        assertEquals(404, exception.getCode());
    }

    @Test
    void listReturnsPagedAuditLogs() {
        Page<AuditLogEntity> page = new Page<>(1, 20);
        page.setRecords(List.of(sampleAuditLog()));
        page.setTotal(1);
        when(auditLogMapper.selectPage(any(), any())).thenReturn(page);

        Page<AuditLogResponse> result = auditLogService.list(1, 20, 2L, "create", "rule");

        assertEquals(1, result.getTotal());
        assertEquals("rule", result.getRecords().get(0).getResourceType());
    }
}
