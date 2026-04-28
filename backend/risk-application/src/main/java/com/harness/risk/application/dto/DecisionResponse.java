package com.harness.risk.application.dto;

import com.harness.risk.domain.decision.DecisionType;

import java.time.LocalDateTime;

/**
 * 决策响应 DTO
 *
 * @param id           决策 ID
 * @param eventId      关联事件 ID
 * @param decisionType 决策类型
 * @param reason       决策原因
 * @param notes        备注
 * @param decidedBy    决策人用户 ID
 * @param decidedAt    决策时间
 * @param createdAt    创建时间
 * @param updatedAt    更新时间
 * @author harness-agent
 * @since 2026-04-28
 */
public record DecisionResponse(
        Long id,
        Long eventId,
        DecisionType decisionType,
        String reason,
        String notes,
        Long decidedBy,
        LocalDateTime decidedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
