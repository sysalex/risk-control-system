package com.harness.risk.application.dto;

import com.harness.risk.domain.event.RiskEventStatus;
import com.harness.risk.domain.event.RiskLevel;

import java.time.LocalDateTime;

/**
 * 风险事件响应 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public record EventResponse(
        /**
         * 事件 ID。
         */
        Long id,

        /**
         * 触发规则 ID。
         */
        Long ruleId,

        /**
         * 主体类型。
         */
        String subjectType,

        /**
         * 主体 ID。
         */
        String subjectId,

        /**
         * 风险等级。
         */
        RiskLevel riskLevel,

        /**
         * 处理状态。
         */
        RiskEventStatus status,

        /**
         * 事件描述。
         */
        String description,

        /**
         * 触发时间。
         */
        LocalDateTime triggeredAt,

        /**
         * 解决时间。
         */
        LocalDateTime resolvedAt,

        /**
         * 解决人用户 ID。
         */
        Long resolvedBy,

        /**
         * 创建时间。
         */
        LocalDateTime createdAt,

        /**
         * 更新时间。
         */
        LocalDateTime updatedAt
) {
}
