package com.harness.risk.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.CreateEventRequest;
import com.harness.risk.application.dto.EventResponse;
import com.harness.risk.application.dto.UpdateEventRequest;
import com.harness.risk.application.service.impl.RiskEventServiceImpl;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.domain.enums.RiskEventStatusEnums;
import com.harness.risk.domain.enums.RiskLevelEnums;
import com.harness.risk.domain.model.entity.RiskEventEntity;
import com.harness.risk.infrastructure.mapper.RiskEventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link RiskEventServiceImpl} 单元测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@ExtendWith(MockitoExtension.class)
class RiskEventServiceTest {

    @Mock
    private RiskEventMapper riskEventMapper;

    private RiskEventServiceImpl riskEventService;

    @BeforeEach
    void setUp() {
        riskEventService = new RiskEventServiceImpl();
        ReflectionTestUtils.setField(riskEventService, "baseMapper", riskEventMapper);
    }

    private RiskEventEntity sampleEvent() {
        RiskEventEntity event = new RiskEventEntity();
        event.setId(1L);
        event.setRuleId(10L);
        event.setSubjectType("user");
        event.setSubjectId("user-123");
        event.setRiskLevel(RiskLevelEnums.HIGH);
        event.setStatus(RiskEventStatusEnums.PENDING);
        event.setDescription("大额交易异常");
        return event;
    }

    @Test
    void createSuccessReturnsEventResponse() {
        when(riskEventMapper.insert(any())).thenReturn(1);

        EventResponse resp = riskEventService.create(
                new CreateEventRequest(10L, "user", "user-123", RiskLevelEnums.HIGH, "大额交易异常"));

        assertEquals(10L, resp.getRuleId());
        assertEquals(RiskLevelEnums.HIGH, resp.getRiskLevel());
        assertEquals(RiskEventStatusEnums.PENDING, resp.getStatus());
        verify(riskEventMapper).insert(any(RiskEventEntity.class));
    }

    @Test
    void getByIdReturnsEventResponse() {
        when(riskEventMapper.selectById(1L)).thenReturn(sampleEvent());

        EventResponse resp = riskEventService.getById(1L);

        assertEquals("大额交易异常", resp.getDescription());
        assertEquals(RiskEventStatusEnums.PENDING, resp.getStatus());
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(riskEventMapper.selectById(1L)).thenReturn(null);

        AppException e = assertThrows(AppException.class, () -> riskEventService.getById(1L));
        assertEquals(404, e.getCode());
    }

    @Test
    void listReturnsPagedResults() {
        Page<RiskEventEntity> page = new Page<>(1, 20);
        page.setRecords(List.of(sampleEvent()));
        page.setTotal(1);
        when(riskEventMapper.selectPage(any(), any())).thenReturn(page);

        Page<EventResponse> result = riskEventService.list(1, 20, null, null);

        assertEquals(1, result.getTotal());
        assertEquals("大额交易异常", result.getRecords().get(0).getDescription());
    }

    @Test
    void listWithFiltersReturnsFilteredResults() {
        Page<RiskEventEntity> page = new Page<>(1, 20);
        page.setRecords(List.of(sampleEvent()));
        page.setTotal(1);
        when(riskEventMapper.selectPage(any(), any())).thenReturn(page);

        Page<EventResponse> result = riskEventService.list(1, 20, RiskLevelEnums.HIGH, RiskEventStatusEnums.PENDING);

        assertEquals(1, result.getTotal());
        ArgumentCaptor<LambdaQueryWrapper<RiskEventEntity>> wrapperCaptor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(riskEventMapper).selectPage(any(), wrapperCaptor.capture());
    }

    @Test
    void updateSuccessReturnsUpdatedEvent() {
        RiskEventEntity event = sampleEvent();
        when(riskEventMapper.selectById(1L)).thenReturn(event);

        EventResponse resp = riskEventService.update(1L,
                new UpdateEventRequest(RiskEventStatusEnums.INVESTIGATING, "补充说明"));

        assertEquals(RiskEventStatusEnums.INVESTIGATING, resp.getStatus());
        assertEquals("补充说明", resp.getDescription());
        verify(riskEventMapper).updateById(any(RiskEventEntity.class));
    }

    @Test
    void updateFailsWhenEventNotFound() {
        when(riskEventMapper.selectById(1L)).thenReturn(null);

        AppException e = assertThrows(AppException.class,
                () -> riskEventService.update(1L, new UpdateEventRequest(RiskEventStatusEnums.INVESTIGATING, null)));
        assertEquals(404, e.getCode());
    }

    @Test
    void resolveEventSetsResolvedStatus() {
        RiskEventEntity event = sampleEvent();
        when(riskEventMapper.selectById(1L)).thenReturn(event);

        EventResponse resp = riskEventService.resolveEvent(1L, 2L);

        assertEquals(RiskEventStatusEnums.RESOLVED, resp.getStatus());
        assertEquals(2L, resp.getResolvedBy());
        assertNotNull(resp.getResolvedAt());
        verify(riskEventMapper).updateById(any(RiskEventEntity.class));
    }

    @Test
    void resolveAlreadyResolvedEventIsIdempotent() {
        RiskEventEntity event = sampleEvent();
        event.setStatus(RiskEventStatusEnums.RESOLVED);
        event.setResolvedBy(3L);
        when(riskEventMapper.selectById(1L)).thenReturn(event);

        EventResponse resp = riskEventService.resolveEvent(1L, 2L);

        assertEquals(RiskEventStatusEnums.RESOLVED, resp.getStatus());
        assertEquals(3L, resp.getResolvedBy());
    }

    @Test
    void resolveEventThrowsWhenNotFound() {
        when(riskEventMapper.selectById(1L)).thenReturn(null);

        AppException e = assertThrows(AppException.class, () -> riskEventService.resolveEvent(1L, 2L));
        assertEquals(404, e.getCode());
    }
}
