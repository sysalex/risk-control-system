package com.harness.risk.application.dto;

import java.time.LocalDateTime;

/**
 * 风控规则响应 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public record RuleResponse(
        /**
         * 规则 ID。
         */
        Long id,

        /**
         * 规则名称。
         */
        String name,

        /**
         * 规则描述。
         */
        String description,

        /**
         * 规则条件 JSON。
         */
        String conditions,

        /**
         * 触发动作 JSON。
         */
        String actions,

        /**
         * 优先级。
         */
        Integer priority,

        /**
         * 是否启用。
         */
        boolean enabled,

        /**
         * 创建者用户 ID。
         */
        Long creatorId,

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
