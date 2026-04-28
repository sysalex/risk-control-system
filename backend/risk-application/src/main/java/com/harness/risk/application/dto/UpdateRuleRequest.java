package com.harness.risk.application.dto;

import jakarta.validation.constraints.Size;

/**
 * 更新风控规则请求 DTO
 *
 * @param name        规则名称（可选）
 * @param description 规则描述（可选）
 * @param conditions  规则条件 JSON（可选）
 * @param actions     触发动作 JSON（可选）
 * @param priority    优先级（可选）
 * @author harness-agent
 * @since 2026-04-27
 */
public record UpdateRuleRequest(
        @Size(max = 128, message = "规则名称长度不能超过 128")
        String name,

        @Size(max = 512, message = "规则描述长度不能超过 512")
        String description,

        String conditions,

        String actions,

        Integer priority
) {
}
