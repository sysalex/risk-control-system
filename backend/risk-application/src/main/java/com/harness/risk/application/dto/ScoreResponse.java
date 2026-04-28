package com.harness.risk.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 风险评分响应。 */
public record ScoreResponse(
        Long id,
        Long eventId,
        String subjectType,
        String subjectId,
        BigDecimal score,
        BigDecimal maxScore,
        String dimensions,
        LocalDateTime evaluatedAt,
        Long evaluatorId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
