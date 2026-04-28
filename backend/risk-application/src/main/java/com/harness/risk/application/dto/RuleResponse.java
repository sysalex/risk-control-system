package com.harness.risk.application.dto;

import java.time.LocalDateTime;

/** 风控规则响应。 */
public record RuleResponse(
        Long id,
        String name,
        String description,
        String conditions,
        String actions,
        Integer priority,
        boolean enabled,
        Long creatorId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
