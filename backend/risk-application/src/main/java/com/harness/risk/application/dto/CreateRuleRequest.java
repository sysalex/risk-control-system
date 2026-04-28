package com.harness.risk.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 创建风控规则请求 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public record CreateRuleRequest(
        /**
         * 规则名称。
         */
        @NotBlank(message = "规则名称不能为空")
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
         * 优先级，数值越小优先级越高。
         */
        @NotNull(message = "优先级不能为空")
        Integer priority
) {
}
