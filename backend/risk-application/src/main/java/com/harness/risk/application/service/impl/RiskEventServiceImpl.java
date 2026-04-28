package com.harness.risk.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.harness.risk.application.dto.CreateEventRequest;
import com.harness.risk.application.dto.EventResponse;
import com.harness.risk.application.dto.UpdateEventRequest;
import com.harness.risk.application.service.RiskEventService;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.domain.enums.RiskEventStatusEnums;
import com.harness.risk.domain.enums.RiskLevelEnums;
import com.harness.risk.domain.model.entity.RiskEventEntity;
import com.harness.risk.infrastructure.mapper.RiskEventMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 风险事件服务实现
 * <p>
 * 继承 {@link ServiceImpl} 获得 MyBatis-Plus 基础 CRUD，
 * 自定义方法覆盖默认行为或补充业务规则。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Service
public class RiskEventServiceImpl extends ServiceImpl<RiskEventMapper, RiskEventEntity> implements RiskEventService {

    @Override
    @Transactional
    public EventResponse create(CreateEventRequest request) {
        RiskEventEntity event = new RiskEventEntity();
        event.setRuleId(request.getRuleId());
        event.setSubjectType(request.getSubjectType());
        event.setSubjectId(request.getSubjectId());
        event.setRiskLevel(request.getRiskLevel());
        event.setDescription(request.getDescription());
        event.setStatus(RiskEventStatusEnums.PENDING);
        event.setTriggeredAt(LocalDateTime.now());
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        save(event);

        return toResponse(event);
    }

    @Override
    public EventResponse getById(Long id) {
        RiskEventEntity event = findOrThrow(id);
        return toResponse(event);
    }

    @Override
    public Page<EventResponse> list(int page, int limit, RiskLevelEnums riskLevel, RiskEventStatusEnums status) {
        LambdaQueryWrapper<RiskEventEntity> wrapper = new LambdaQueryWrapper<>();
        if (riskLevel != null) {
            wrapper.eq(RiskEventEntity::getRiskLevel, riskLevel);
        }
        if (status != null) {
            wrapper.eq(RiskEventEntity::getStatus, status);
        }
        wrapper.orderByDesc(RiskEventEntity::getTriggeredAt);

        Page<RiskEventEntity> result = page(new Page<>(page, limit), wrapper);
        List<EventResponse> records = result.getRecords().stream()
                .map(this::toResponse)
                .toList();
        Page<EventResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(records);
        return responsePage;
    }

    @Override
    @Transactional
    public EventResponse update(Long id, UpdateEventRequest request) {
        RiskEventEntity event = findOrThrow(id);

        if (request.getStatus() != null) {
            event.setStatus(request.getStatus());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        event.setUpdatedAt(LocalDateTime.now());
        updateById(event);

        return toResponse(event);
    }

    @Override
    @Transactional
    public EventResponse resolveEvent(Long id, Long resolvedBy) {
        RiskEventEntity event = findOrThrow(id);

        // 已解决的事件保持幂等，不修改解决人
        if (event.getStatus() != RiskEventStatusEnums.RESOLVED) {
            event.setStatus(RiskEventStatusEnums.RESOLVED);
            event.setResolvedBy(resolvedBy);
            event.setResolvedAt(LocalDateTime.now());
            event.setUpdatedAt(LocalDateTime.now());
            updateById(event);
        }

        return toResponse(event);
    }

    private RiskEventEntity findOrThrow(Long id) {
        RiskEventEntity event = super.getById(id);
        if (event == null) {
            throw AppException.notFound("Event");
        }
        return event;
    }

    private EventResponse toResponse(RiskEventEntity event) {
        return new EventResponse(
                event.getId(),
                event.getRuleId(),
                event.getSubjectType(),
                event.getSubjectId(),
                event.getRiskLevel(),
                event.getStatus(),
                event.getDescription(),
                event.getTriggeredAt(),
                event.getResolvedAt(),
                event.getResolvedBy(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
