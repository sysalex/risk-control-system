package com.harness.risk.application.dto;

import com.harness.risk.domain.decision.DecisionType;

import java.time.LocalDateTime;

/**
 * 决策响应 DTO。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
public record DecisionResponse(
        /**
         * 决策 ID。
         */
        Long id,

        /**
         * 关联事件 ID。
         */
        Long eventId,

        /**
         * 决策类型。
         */
        DecisionType decisionType,

        /**
         * 决策原因。
         */
        String reason,

        /**
         * 备注。
         */
        String notes,

        /**
         * 决策人用户 ID。
         */
        Long decidedBy,

        /**
         * 决策时间。
         */
        LocalDateTime decidedAt,

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
