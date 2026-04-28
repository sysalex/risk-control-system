package com.harness.risk.application.dto;

import jakarta.validation.constraints.Size;

/**
 * 更新风控规则请求 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public record UpdateRuleRequest(
        /**
         * 规则名称。
         */
        @Size(max = 128, message = "规则名称长度不能超过 128")
        String name,

        /**
         * 规则描述。
         */
        @Size(max = 512, message = "规则描述长度不能超过 512")
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
        Integer priority
) {
}
