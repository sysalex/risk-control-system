package com.harness.risk.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 创建风控规则请求。 */
public record CreateRuleRequest(
        @NotBlank(message = "规则名称不能为空")
        @Size(max = 128, message = "规则名称长度不能超过 128")
        String name,

        @Size(max = 512, message = "规则描述长度不能超过 512")
        String description,

        String conditions,

        String actions,

        @NotNull(message = "优先级不能为空")
        Integer priority
) {
}
