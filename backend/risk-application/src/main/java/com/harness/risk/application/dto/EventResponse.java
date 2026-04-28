package com.harness.risk.application.dto;

import com.harness.risk.domain.event.RiskEventStatus;
import com.harness.risk.domain.event.RiskLevel;

import java.time.LocalDateTime;

/** 风险事件响应。 */
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
