package com.harness.risk.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.harness.risk.application.dto.AuditLogResponse;
import com.harness.risk.application.dto.CreateAuditLogRequest;
import com.harness.risk.application.service.AuditLogService;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.domain.model.entity.AuditLogEntity;
import com.harness.risk.infrastructure.mapper.AuditLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志服务实现。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLogEntity> implements AuditLogService {

    @Override
    @Transactional
    public AuditLogResponse record(CreateAuditLogRequest request) {
        AuditLogEntity auditLog = new AuditLogEntity();
        auditLog.setUserId(request.getUserId());
        auditLog.setAction(request.getAction());
        auditLog.setResourceType(request.getResourceType());
        auditLog.setResourceId(request.getResourceId());
        auditLog.setOldValues(request.getOldValues());
        auditLog.setNewValues(request.getNewValues());
        auditLog.setIpAddress(request.getIpAddress());
        auditLog.setCreatedAt(LocalDateTime.now());
        save(auditLog);
        return toResponse(auditLog);
    }

    @Override
    public AuditLogResponse getById(Long id) {
        AuditLogEntity auditLog = super.getById(id);
        if (auditLog == null) {
            throw AppException.notFound("AuditLog");
        }
        return toResponse(auditLog);
    }

    @Override
    public Page<AuditLogResponse> list(int page, int limit, Long userId, String action, String resourceType) {
        LambdaQueryWrapper<AuditLogEntity> wrapper = new LambdaQueryWrapper<AuditLogEntity>()
                .eq(userId != null, AuditLogEntity::getUserId, userId)
                .eq(StringUtils.hasText(action), AuditLogEntity::getAction, action)
                .eq(StringUtils.hasText(resourceType), AuditLogEntity::getResourceType, resourceType)
                .orderByDesc(AuditLogEntity::getCreatedAt);
        Page<AuditLogEntity> result = page(new Page<>(page, limit), wrapper);
        List<AuditLogResponse> records = result.getRecords().stream()
                .map(this::toResponse)
                .toList();
        Page<AuditLogResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(records);
        return responsePage;
    }

    private AuditLogResponse toResponse(AuditLogEntity auditLog) {
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getUserId(),
                auditLog.getAction(),
                auditLog.getResourceType(),
                auditLog.getResourceId(),
                auditLog.getOldValues(),
                auditLog.getNewValues(),
                auditLog.getCreatedAt(),
                auditLog.getIpAddress()
        );
    }
}
