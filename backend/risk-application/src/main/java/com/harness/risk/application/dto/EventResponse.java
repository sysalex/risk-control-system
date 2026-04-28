package com.harness.risk.application.dto;

import com.harness.risk.domain.event.RiskEventStatus;
import com.harness.risk.domain.event.RiskLevel;

import java.time.LocalDateTime;

/**
 * 风险事件响应 DTO
 *
 * @param id          事件 ID
 * @param ruleId      触发规则的 ID
 * @param subjectType 主体类型
 * @param subjectId   主体 ID
 * @param riskLevel   风险等级
 * @param status      处理状态
 * @param description 事件描述
 * @param triggeredAt 触发时间
 * @param resolvedAt  解决时间
 * @param resolvedBy  解决人用户 ID
 * @param createdAt   创建时间
 * @param updatedAt   更新时间
 * @author harness-agent
 * @since 2026-04-27
 */
public record EventResponse(
        Long id,
        Long ruleId,
        String subjectType,
        String subjectId,
        RiskLevel riskLevel,
        RiskEventStatus status,
        String description,
        LocalDateTime triggeredAt,
        LocalDateTime resolvedAt,
        Long resolvedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
