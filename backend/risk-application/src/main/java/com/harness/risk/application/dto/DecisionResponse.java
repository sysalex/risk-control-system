package com.harness.risk.application.dto;

import com.harness.risk.domain.decision.DecisionType;

import java.time.LocalDateTime;

/** 决策响应。 */
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
