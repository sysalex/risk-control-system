package com.harness.risk.application.dto;

import java.time.LocalDateTime;

/**
 * 风控规则响应 DTO
 *
 * @param id          规则 ID
 * @param name        规则名称
 * @param description 规则描述
 * @param conditions  规则条件 JSON
 * @param actions     触发动作 JSON
 * @param priority    优先级
 * @param enabled     是否启用
 * @param creatorId   创建者用户 ID
 * @param createdAt   创建时间
 * @param updatedAt   更新时间
 * @author harness-agent
 * @since 2026-04-27
 */
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
