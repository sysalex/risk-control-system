package com.harness.risk.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 风控规则响应 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleResponse {
    /**
     * 规则 ID。
     */
    private Long id;

    /**
     * 规则名称。
     */
    private String name;

    /**
     * 规则描述。
     */
    private String description;

    /**
     * 规则条件 JSON。
     */
    private String conditions;

    /**
     * 触发动作 JSON。
     */
    private String actions;

    /**
     * 优先级。
     */
    private Integer priority;

    /**
     * 是否启用。
     */
    private boolean enabled;

    /**
     * 创建者用户 ID。
     */
    private Long creatorId;

    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间。
     */
    private LocalDateTime updatedAt;
}
